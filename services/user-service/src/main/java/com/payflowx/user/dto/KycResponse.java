package com.payflowx.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record KycResponse(
        String legalName,
        LocalDate dateOfBirth,
        String nationality,
        String governmentId,
        String kycStatus,
        LocalDateTime verifiedAt,
        String rejectionReason
) {}