package com.payflowx.merchant.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseEvent {

    private String eventId;

    private String correlationId;

    private String aggregateId;

    private String sourceService;
}