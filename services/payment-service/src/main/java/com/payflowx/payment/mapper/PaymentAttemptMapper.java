package com.payflowx.payment.mapper;

import com.payflowx.payment.dto.response.PaymentAttemptResponse;
import com.payflowx.payment.entity.PaymentAttempt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentAttemptMapper {

    PaymentAttemptResponse toResponse(PaymentAttempt attempt);
}