package com.payflowx.user.mapper;

import com.payflowx.user.dto.AddressResponse;
import com.payflowx.user.dto.KycResponse;
import com.payflowx.user.dto.UserResponse;
import com.payflowx.user.entity.User;
import com.payflowx.user.entity.UserAddress;
import com.payflowx.user.entity.UserKyc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", source = "profile.fullName")
    @Mapping(target = "phone", source = "profile.phone")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    @Mapping(target = "dateOfBirth", source = "profile.dateOfBirth")
    @Mapping(target = "nationality", source = "profile.nationality")
    @Mapping(target = "accountStatus", expression = "java(user.getAccountStatus() != null ? user.getAccountStatus().name() : null)")
    @Mapping(target = "addresses", source = "addresses")
    @Mapping(target = "onboardingCompleted", source = "onboardingCompleted")
    @Mapping(target = "isVerified", source = "verified")
    UserResponse toResponse(User user);

    @Mapping(target = "defaultAddress", source = "defaultAddress")
    @Mapping(target = "addressLine2", source = "addressLine2")
    AddressResponse toAddressResponse(UserAddress address);

    @Mapping(target = "kycStatus", expression = "java(kyc.getKycStatus().name())")
    KycResponse toKycResponse(UserKyc kyc);

    List<AddressResponse> toAddressList(List<UserAddress> addresses);
}