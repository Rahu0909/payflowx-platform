package com.payflowx.user.serviceImpl;

import com.payflowx.user.constant.ErrorCode;
import com.payflowx.user.dto.AdminUserResponse;
import com.payflowx.user.dto.UpdateUserRequest;
import com.payflowx.user.dto.UpdateUserStatusRequest;
import com.payflowx.user.dto.UserResponse;
import com.payflowx.user.dto.event.UserNotificationEvent;
import com.payflowx.user.entity.User;
import com.payflowx.user.entity.UserProfile;
import com.payflowx.user.entity.enums.AccountStatus;
import com.payflowx.user.entity.enums.UserEventType;
import com.payflowx.user.exception.BusinessValidationException;
import com.payflowx.user.mapper.UserMapper;
import com.payflowx.user.repository.UserRepository;
import com.payflowx.user.service.UserNotificationPublisher;
import com.payflowx.user.service.UserService;
import com.payflowx.user.util.ProfileValidationUtil;
import com.payflowx.user.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserNotificationPublisher userNotificationPublisher;
    private final UserMetricsService userMetricsService;

    @Override
    public UserResponse getCurrentUser(UUID userId) {
        log.info("Fetching user with authUserId={}", userId);
        User user = userRepository.findCompleteByAuthUserIdAndDeletedFalse(userId).orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void createUserInternal(UUID authUserId, String email) {
        log.info("Creating user for authUserId={}", authUserId);
        if (userRepository.existsByAuthUserId(authUserId)) {
            log.info("User already exists for authUserId={}", authUserId);
            return;
        }
        User user = User.builder().authUserId(authUserId).email(email).accountStatus(AccountStatus.ACTIVE).build();
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        user.setProfile(profile);
        User savedUser = userRepository.save(user);
        publishUserCreatedEvent(savedUser);
        userMetricsService.incrementUsersCreated();
        log.info("User created successfully for authUserId={}", authUserId);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateUserRequest request) {
        log.info("Updating profile for authUserId={}", userId);
        User user = userRepository.findWithProfileAndKycByAuthUserIdAndDeletedFalse(userId).orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        if (user.getKyc() != null && "VERIFIED".equals(user.getKyc().getKycStatus().name())) {
            throw new BusinessValidationException(ErrorCode.PROFILE_LOCKED_KYC);
        }
        ProfileValidationUtil.validateDob(request.dateOfBirth());
        ProfileValidationUtil.validateAvatarUrl(request.avatarUrl());
        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            user.setProfile(profile);
        }
        if (request.fullName() != null) {
            profile.setFullName(request.fullName());
        }
        if (request.avatarUrl() != null) {
            profile.setAvatarUrl(request.avatarUrl());
        }
        if (request.phone() != null) {
            profile.setPhone(request.phone());
        }
        if (request.dateOfBirth() != null) {
            profile.setDateOfBirth(request.dateOfBirth());
        }
        if (request.nationality() != null) {
            profile.setNationality(request.nationality());
        }
        if (profile.getFullName() != null && profile.getPhone() != null && profile.getDateOfBirth() != null && profile.getNationality() != null) {
            user.setOnboardingCompleted(true);
        }
        log.info("Profile updated successfully for authUserId={}", userId);
        return userMapper.toResponse(user);
    }

    @Override
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findByDeletedFalse(pageable).map(this::mapToAdminResponse);
    }

    @Override
    public AdminUserResponse getUserById(UUID userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        return mapToAdminResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        AccountStatus newStatus = request.status();
        AccountStatus currentStatus = user.getAccountStatus();
        validateStatusTransition(currentStatus, newStatus);
        if (currentStatus == newStatus) {
            throw new BusinessValidationException(ErrorCode.STATUS_ALREADY_SET);
        }
        user.setAccountStatus(newStatus);
        user.setStatusChangedAt(LocalDateTime.now());
        user.setStatusChangedBy(SecurityUtil.getCurrentUserId());
        user.setStatusReason(request.reason());
        User savedUser = userRepository.save(user);
        publishAccountStatusEvent(savedUser);
        return mapToAdminResponse(savedUser);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    private void validateStatusTransition(AccountStatus current, AccountStatus target) {
        if (current == target) {
            throw new BusinessValidationException(ErrorCode.STATUS_ALREADY_SET);
        }
        if (current == AccountStatus.BLOCKED && target == AccountStatus.ACTIVE) {
            throw new BusinessValidationException(ErrorCode.BLOCKED_USER_MUST_BE_SUSPENDED_FIRST);
        }
    }

    private AdminUserResponse mapToAdminResponse(User user) {
        return new AdminUserResponse(user.getId(), user.getEmail(), user.getAccountStatus(), user.isVerified(), user.isOnboardingCompleted());
    }

    private void publishUserCreatedEvent(User user) {
        UserNotificationEvent event = UserNotificationEvent.builder().eventId(UUID.randomUUID()).userId(user.getId()).email(user.getEmail()).fullName(user.getProfile() != null ? user.getProfile().getFullName() : null).eventType(UserEventType.USER_CREATED.name()).message("User account created successfully").occurredAt(LocalDateTime.now()).build();
        userNotificationPublisher.publish(event);
    }

    private void publishAccountStatusEvent(User user) {
        UserEventType eventType = null;
        if (user.getAccountStatus() == AccountStatus.BLOCKED) {
            userMetricsService.incrementUsersBlocked();
            eventType = UserEventType.USER_ACCOUNT_BLOCKED;
        }
        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            eventType = UserEventType.USER_ACCOUNT_SUSPENDED;
        }
        if (eventType == null) {
            return;
        }
        UserNotificationEvent event = UserNotificationEvent.builder().eventId(UUID.randomUUID()).userId(user.getId()).email(user.getEmail()).fullName(user.getProfile() != null ? user.getProfile().getFullName() : null).eventType(eventType.name()).message("User account status updated").occurredAt(LocalDateTime.now()).build();
        userNotificationPublisher.publish(event);
    }
}