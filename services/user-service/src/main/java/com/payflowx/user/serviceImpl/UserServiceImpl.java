package com.payflowx.user.serviceImpl;

import com.payflowx.user.constant.ApiConstants;
import com.payflowx.user.constant.ErrorCode;
import com.payflowx.user.dto.AdminUserResponse;
import com.payflowx.user.dto.UpdateUserRequest;
import com.payflowx.user.dto.UpdateUserStatusRequest;
import com.payflowx.user.dto.UserResponse;
import com.payflowx.user.entity.User;
import com.payflowx.user.entity.UserProfile;
import com.payflowx.user.entity.enums.AccountStatus;
import com.payflowx.user.exception.BusinessValidationException;
import com.payflowx.user.exception.ResourceNotFoundException;
import com.payflowx.user.mapper.UserMapper;
import com.payflowx.user.repository.UserRepository;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getCurrentUser(UUID userId) {
        log.info("Fetching user with authUserId={}", userId);
        User user = userRepository
                .findCompleteByAuthUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
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
        User user = User.builder()
                .authUserId(authUserId)
                .email(email)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        user.setProfile(profile);
        userRepository.save(user);
        log.info("User created successfully for authUserId={}", authUserId);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateUserRequest request) {
        log.info("Updating profile for authUserId={}", userId);
        User user = userRepository
                .findWithProfileAndKycByAuthUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        /* KYC lock (future-proof)*/
        if (user.getKyc() != null && "VERIFIED".equals(user.getKyc().getKycStatus().name())) {
            throw new BusinessValidationException(ErrorCode.PROFILE_LOCKED_KYC);
        }
        /* Business validations*/
        ProfileValidationUtil.validateDob(request.dateOfBirth());
        ProfileValidationUtil.validateAvatarUrl(request.avatarUrl());
        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            user.setProfile(profile);
        }
        /* Null-safe updates*/
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
        /* Onboarding completion*/
        if (profile.getFullName() != null &&
                profile.getPhone() != null &&
                profile.getDateOfBirth() != null &&
                profile.getNationality() != null) {

            user.setOnboardingCompleted(true);
        }
        log.info("Profile updated successfully for authUserId={}", userId);
        return userMapper.toResponse(user);
    }

    @Override
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findByDeletedFalse(pageable)
                .map(this::mapToAdminResponse);
    }

    @Override
    public AdminUserResponse getUserById(UUID userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        return mapToAdminResponse(user);
    }

    @Override
    public AdminUserResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        AccountStatus newStatus = request.status();
        AccountStatus currentStatus = user.getAccountStatus();
        validateStatusTransition(currentStatus, newStatus);
        /* Prevent redundant updates*/
        if (currentStatus == newStatus) {
            throw new BusinessValidationException(ErrorCode.STATUS_ALREADY_SET);
        }
        /* Optional: transition rules (recommended)*/
        user.setAccountStatus(newStatus);
        user.setStatusChangedAt(LocalDateTime.now());
        user.setStatusChangedBy(SecurityUtil.getCurrentUserId());
        user.setStatusReason(request.reason());
        User saved = userRepository.save(user);
        return mapToAdminResponse(saved);
    }

    @Override
    public void deactivateUser(UUID userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /*Transition Guard (important)*/
    private void validateStatusTransition(AccountStatus current, AccountStatus target) {
        if (current == target) {
            throw new BusinessValidationException(ErrorCode.STATUS_ALREADY_SET);
        }

        /* BLOCKED → ACTIVE not allowed directly*/
        if (current == AccountStatus.BLOCKED && target == AccountStatus.ACTIVE) {
            throw new BusinessValidationException(ErrorCode.BLOCKED_USER_MUST_BE_SUSPENDED_FIRST);
        }
    }

    private AdminUserResponse mapToAdminResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getAccountStatus(),
                user.isVerified(),
                user.isOnboardingCompleted()
        );
    }
}