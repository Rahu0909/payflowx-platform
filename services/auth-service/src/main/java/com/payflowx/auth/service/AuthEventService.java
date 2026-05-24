package com.payflowx.auth.service;

import com.payflowx.auth.entity.User;
import com.payflowx.auth.enums.AuthEventType;

public interface AuthEventService {

    void publishEvent(User user, AuthEventType eventType, Object payload);
}