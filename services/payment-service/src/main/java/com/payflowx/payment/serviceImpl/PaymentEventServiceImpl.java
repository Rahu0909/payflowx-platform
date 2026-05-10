package com.payflowx.payment.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.entity.PaymentEvent;
import com.payflowx.payment.enums.PaymentEventType;
import com.payflowx.payment.repository.PaymentEventRepository;
import com.payflowx.payment.service.PaymentEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventServiceImpl implements PaymentEventService {
    private final PaymentEventRepository paymentEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishEvent(Payment payment, PaymentEventType eventType) {
        try {
            Map<String, Object> payload = Map.of("paymentId",
                    payment.getId(), "paymentReference",
                    payment.getPaymentReference(), "orderId",
                    payment.getOrderId(), "merchantId", payment.getMerchantId(),
                    "userId", payment.getUserId(), "amount", payment.getAmount(),
                    "currency", payment.getCurrency(), "status", payment.getStatus(),
                    "eventType", eventType.name());
            PaymentEvent event = PaymentEvent.builder().paymentId(payment.getId()).merchantId(payment.getMerchantId())
                    .eventType(eventType).payload(objectMapper.writeValueAsString(payload)).build();
            paymentEventRepository.save(event);
            log.info("Payment event published paymentId={} eventType={}", payment.getId(), eventType);
        } catch (Exception ex) {
            log.error("Failed to publish payment event paymentId={}", payment.getId(), ex);
        }
    }
}