package com.payflowx.order.mapper;

import com.payflowx.order.dto.response.OrderResponse;
import com.payflowx.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "currency", expression = "java(order.getCurrency().name())")

    @Mapping(target = "status", expression = "java(order.getStatus().name())")

    @Mapping(target = "createdAt", expression = "java(order.getCreatedAt())")
    OrderResponse toResponse(Order order);
}