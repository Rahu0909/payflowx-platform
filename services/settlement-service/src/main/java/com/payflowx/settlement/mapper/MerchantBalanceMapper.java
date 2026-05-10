package com.payflowx.settlement.mapper;

import com.payflowx.settlement.dto.response.MerchantBalanceResponse;
import com.payflowx.settlement.entity.MerchantBalance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MerchantBalanceMapper {

    MerchantBalanceResponse toResponse(MerchantBalance balance);
}