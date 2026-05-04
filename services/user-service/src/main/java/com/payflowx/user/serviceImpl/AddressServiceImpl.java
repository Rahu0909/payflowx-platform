package com.payflowx.user.serviceImpl;

import com.payflowx.user.dto.*;
import com.payflowx.user.entity.User;
import com.payflowx.user.entity.UserAddress;
import com.payflowx.user.exception.BusinessValidationException;
import com.payflowx.user.exception.ResourceNotFoundException;
import com.payflowx.user.mapper.UserMapper;
import com.payflowx.user.repository.UserAddressRepository;
import com.payflowx.user.repository.UserRepository;
import com.payflowx.user.service.AddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private static final int MAX_ADDRESS_LIMIT = 5;

    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;
    private final UserMapper mapper;

    /* ---------------- CREATE ---------------- */
    @Override
    @Transactional
    public AddressResponse createAddress(UUID authUserId, CreateAddressRequest request) {
        User user = getUser(authUserId);
        long count = addressRepository.countByUserId(user.getId());
        if (count >= MAX_ADDRESS_LIMIT) {
            throw new BusinessValidationException("Maximum address limit reached");
        }
        if (request.defaultAddress()) {
            clearExistingDefault(user.getId());
        }
        UserAddress address = UserAddress.builder()
                .user(user)
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .postalCode(request.postalCode())
                .defaultAddress(request.defaultAddress() || count == 0) // first address default
                .build();
        addressRepository.save(address);
        log.info("Address created for userId={}, addressId={}", user.getId(), address.getId());
        return mapper.toAddressResponse(address);
    }

    /* ---------------- READ ---------------- */
    @Override
    public List<AddressResponse> getUserAddresses(UUID authUserId) {
        User user = getUser(authUserId);
        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(mapper::toAddressResponse)
                .toList();
    }

    @Override
    public AddressResponse getAddressById(UUID authUserId, UUID addressId) {
        User user = getUser(authUserId);
        UserAddress address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return mapper.toAddressResponse(address);
    }

    @Override
    public AddressResponse getDefaultAddress(UUID authUserId) {
        User user = getUser(authUserId);

        UserAddress address = addressRepository.findByUserIdAndDefaultAddressTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Default address not found"));

        return mapper.toAddressResponse(address);
    }

    /* ---------------- UPDATE ---------------- */
    @Override
    @Transactional
    public AddressResponse updateAddress(UUID authUserId, UUID addressId, UpdateAddressRequest request) {
        User user = getUser(authUserId);
        UserAddress address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (request.defaultAddress() != null && request.defaultAddress()) {
            clearExistingDefault(user.getId());
            address.setDefaultAddress(true);
        }
        if (request.addressLine1() != null) address.setAddressLine1(request.addressLine1());
        if (request.addressLine2() != null) address.setAddressLine2(request.addressLine2());
        if (request.city() != null) address.setCity(request.city());
        if (request.state() != null) address.setState(request.state());
        if (request.country() != null) address.setCountry(request.country());
        if (request.postalCode() != null) address.setPostalCode(request.postalCode());
        log.info("Address updated addressId={}", addressId);
        return mapper.toAddressResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(UUID authUserId, UUID addressId) {
        User user = getUser(authUserId);
        UserAddress address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        clearExistingDefault(user.getId());
        address.setDefaultAddress(true);
        log.info("Default address set addressId={}", addressId);
        return mapper.toAddressResponse(address);
    }

    /* ---------------- DELETE ---------------- */
    @Override
    @Transactional
    public void deleteAddress(UUID authUserId, UUID addressId) {
        User user = getUser(authUserId);
        UserAddress address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        boolean wasDefault = address.isDefaultAddress();
        addressRepository.delete(address);
        if (wasDefault) {
            addressRepository.findByUserId(user.getId())
                    .stream()
                    .findFirst()
                    .ifPresent(addr -> addr.setDefaultAddress(true));
        }
        log.info("Address deleted addressId={}", addressId);
    }

    @Override
    public long countAddresses(UUID authUserId) {
        User user = getUser(authUserId);
        return addressRepository.countByUserId(user.getId());
    }

    private User getUser(UUID authUserId) {
        return userRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void clearExistingDefault(UUID userId) {
        addressRepository.findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(addr -> addr.setDefaultAddress(false));
    }
}