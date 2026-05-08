package com.payflowx.merchant.mapper;

import com.payflowx.merchant.dto.response.WebhookResponse;
import com.payflowx.merchant.entity.MerchantWebhook;
import com.payflowx.merchant.enums.WebhookEventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface WebhookMapper {

    @Mapping(target = "status", expression = "java(webhook.getStatus().name())")

    @Mapping(target = "subscribedEvents", expression = "java(mapEvents(webhook.getSubscribedEvents()))")

    @Mapping(target = "createdAt", expression = "java(webhook.getCreatedAt())")
    WebhookResponse toResponse(MerchantWebhook webhook);

    default Set<String> mapEvents(Set<WebhookEventType> events) {
        if (events == null) {
            return Collections.emptySet();
        }
        return events.stream().map(Enum::name).collect(Collectors.toSet());
    }
}