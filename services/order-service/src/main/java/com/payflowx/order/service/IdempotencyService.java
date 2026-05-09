package com.payflowx.order.service;

import com.payflowx.order.entity.IdempotencyRecord;

import java.util.UUID;

public interface IdempotencyService {

    IdempotencyRecord validateOrGet(String idempotencyKey);

    void saveRecord(String idempotencyKey, UUID resourceId, String resourceType);
}