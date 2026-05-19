package com.payflowx.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseEvent {

    private String eventId;

    private String correlationId;

    private String sourceService;

    private String aggregateId;
}