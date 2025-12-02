package com.louly.soft.bookstore.order.domain;

import com.louly.soft.bookstore.order.domain.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OrderItemMapper {

    OrderItem toDto(OrderItemEntity orderItemEntity);

    OrderItemEntity toEntity(OrderItem orderItem);
}
