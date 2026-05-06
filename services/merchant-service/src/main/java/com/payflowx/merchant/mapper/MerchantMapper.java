package com.payflowx.merchant.mapper;

import com.payflowx.merchant.dto.response.AdminMerchantResponse;
import com.payflowx.merchant.dto.response.MerchantResponse;
import com.payflowx.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MerchantMapper {

    @Mapping(target = "status",
            expression = "java(merchant.getStatus().name())")
    MerchantResponse toResponse(Merchant merchant);


    @Mapping(target = "status",
            expression = "java(merchant.getStatus().name())")
    @Mapping(target = "createdAt",
            expression = "java(merchant.getCreatedAt())")
    @Mapping(target = "updatedAt",
            expression = "java(merchant.getUpdatedAt())")
    AdminMerchantResponse toAdminResponse(Merchant merchant);
}