package com.payflowx.user.service;

import com.payflowx.user.dto.AddressResponse;
import com.payflowx.user.dto.CreateAddressRequest;
import com.payflowx.user.dto.UpdateAddressRequest;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    /* Create */
    AddressResponse createAddress(UUID authUserId, CreateAddressRequest request);

    /* Read */
    List<AddressResponse> getUserAddresses(UUID authUserId);

    AddressResponse getAddressById(UUID authUserId, UUID addressId);

    AddressResponse getDefaultAddress(UUID authUserId);

    /* Update */
    AddressResponse updateAddress(UUID authUserId, UUID addressId, UpdateAddressRequest request);

    AddressResponse setDefaultAddress(UUID authUserId, UUID addressId);

    /* Delete */
    void deleteAddress(UUID authUserId, UUID addressId);

    /* Utility */
    long countAddresses(UUID authUserId);
}