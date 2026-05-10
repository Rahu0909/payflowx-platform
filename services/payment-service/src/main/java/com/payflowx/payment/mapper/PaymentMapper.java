package com.payflowx.payment.mapper;

import com.payflowx.payment.dto.response.PaymentResponse;
import com.payflowx.payment.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponse toResponse(Payment payment);
}