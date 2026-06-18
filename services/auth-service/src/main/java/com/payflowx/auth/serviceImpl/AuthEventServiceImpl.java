package com.payflowx.auth.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.auth.entity.AuthEvent;
import com.payflowx.auth.entity.User;
import com.payflowx.auth.enums.AuthEventType;
import com.payflowx.auth.repository.AuthEventRepository;
import com.payflowx.auth.service.AuthEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthEventServiceImpl implements AuthEventService {
    private final AuthEventRepository authEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishEvent(User user, AuthEventType eventType, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            AuthEvent authEvent = AuthEvent.builder().userId(user.getId()).eventType(eventType).payload(jsonPayload).correlationId(MDC.get("correlationId")).build();
            authEventRepository.save(authEvent);
            log.info("Auth event stored userId={} eventType={}", user.getId(), eventType);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize auth event userId={} type={}", user.getId(), eventType, ex);
        }
    }
}