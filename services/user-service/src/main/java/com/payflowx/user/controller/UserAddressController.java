package com.payflowx.user.controller;

import com.payflowx.user.dto.AddressResponse;
import com.payflowx.user.dto.ApiResponse;
import com.payflowx.user.dto.CreateAddressRequest;
import com.payflowx.user.dto.UpdateAddressRequest;
import com.payflowx.user.service.AddressService;
import com.payflowx.user.util.HeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final AddressService service;

    @PostMapping
    public ApiResponse<AddressResponse> create(
            HttpServletRequest request,
            @Valid @RequestBody CreateAddressRequest body
    ) {
        UUID userId = HeaderUtil.extractUserId(request);
        return ApiResponse.success(service.createAddress(userId, body), "Address created");
    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> getAll(HttpServletRequest request) {
        UUID userId = HeaderUtil.extractUserId(request);
        return ApiResponse.success(service.getUserAddresses(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> getById(
            HttpServletRequest request,
            @PathVariable UUID id
    ) {
        UUID userId = HeaderUtil.extractUserId(request);
        return ApiResponse.success(service.getAddressById(userId, id));
    }

    @GetMapping("/default")
    public ApiResponse<AddressResponse> getDefault(HttpServletRequest request) {
        UUID userId = HeaderUtil.extractUserId(request);
        return ApiResponse.success(service.getDefaultAddress(userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody UpdateAddressRequest body
    ) {
        UUID userId = HeaderUtil.extractUserId(request);
        return ApiResponse.success(service.updateAddress(userId, id, body), "Address updated");
    }

    @PatchMapping("/{id}/default")
    public ApiResponse<AddressResponse> setDefault(
            HttpServletRequest request,
            @PathVariable UUID id
    ) {
        UUID userId = HeaderUtil.extractUserId(request);
        return ApiResponse.success(service.setDefaultAddress(userId, id), "Default address set");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            HttpServletRequest request,
            @PathVariable UUID id
    ) {
        UUID userId = HeaderUtil.extractUserId(request);
        service.deleteAddress(userId, id);
        return ApiResponse.success(null, "Address deleted");
    }
}