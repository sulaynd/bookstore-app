package com.louly.soft.bookstore.order.domain;

import com.louly.soft.bookstore.order.domain.models.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItem toDto(OrderItemEntity orderItemEntity);

    OrderItemEntity toEntity(OrderItem orderItem);
}
