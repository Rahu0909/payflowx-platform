package com.payflowx.user.dto;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String addressLine1,
        String city,
        String state,
        String country,
        String postalCode,
        boolean isPrimary
) {}