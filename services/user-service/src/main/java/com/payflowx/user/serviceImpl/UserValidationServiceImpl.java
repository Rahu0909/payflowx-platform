package com.payflowx.user.serviceImpl;

import com.payflowx.user.constant.ErrorCode;
import com.payflowx.user.dto.EligibilityResponse;
import com.payflowx.user.entity.User;
import com.payflowx.user.entity.enums.AccountStatus;
import com.payflowx.user.exception.BusinessValidationException;
import com.payflowx.user.repository.UserAddressRepository;
import com.payflowx.user.repository.UserRepository;
import com.payflowx.user.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {

    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;

    @Override
    public EligibilityResponse validateUserForPayment(UUID userId) {
        try {
            validateOrThrow(userId);
            return new EligibilityResponse(true, "ELIGIBLE");
        } catch (BusinessValidationException ex) {
            return new EligibilityResponse(false, ex.getMessage());
        }
    }

    @Override
    public void validateOrThrow(UUID userId) {

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new BusinessValidationException(ErrorCode.USER_DEACTIVATED);
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BusinessValidationException(ErrorCode.USER_ACCOUNT_NOT_ACTIVE);
        }

        if (!user.isVerified()) {
            throw new BusinessValidationException(ErrorCode.KYC_NOT_VERIFIED);
        }

        if (!user.isOnboardingCompleted()) {
            throw new BusinessValidationException(ErrorCode.ONBOARDING_INCOMPLETE);
        }

        boolean hasDefaultAddress =
                addressRepository.existsByUserIdAndDefaultAddressTrue(userId);

        if (!hasDefaultAddress) {
            throw new BusinessValidationException(ErrorCode.DEFAULT_ADDRESS_REQUIRED);
        }
    }
}