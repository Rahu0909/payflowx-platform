package com.payflowx.payment.mapper;

import com.payflowx.payment.dto.response.RefundResponse;
import com.payflowx.payment.entity.Refund;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    RefundResponse toResponse(Refund refund);
}