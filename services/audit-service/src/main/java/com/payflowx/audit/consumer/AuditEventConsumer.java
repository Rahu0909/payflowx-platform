package com.payflowx.audit.consumer;

import com.payflowx.audit.config.AuditRabbitMqConstants;
import com.payflowx.audit.dto.AuditEventMessage;
import com.payflowx.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditEventService auditEventService;

    @RabbitListener(queues = AuditRabbitMqConstants.AUDIT_QUEUE)
    public void consume(AuditEventMessage message) {

        log.info("Audit event received eventId={} type={}", message.eventId(), message.eventType());

        auditEventService.saveEvent(message);
    }
}