package com.louly.soft.bookstore.order.domain;

import com.louly.soft.bookstore.order.domain.models.OrderDTO;
import com.louly.soft.bookstore.order.domain.models.OrderItem;
import com.louly.soft.bookstore.order.domain.models.OrderRequest;
import com.louly.soft.bookstore.order.domain.models.OrderStatus;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

// @Mapper(
//        unmappedTargetPolicy = ReportingPolicy.IGNORE,
//        componentModel = "spring",
//        uses = {OrderItemMapper.class})
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OrderMapper {

    OrderDTO toDto(OrderEntity order);

    OrderEntity toEntity(OrderRequest request);

    @AfterMapping
    default void generateOrderNumber(@MappingTarget OrderEntity order) {
        if (order.getOrderNumber() == null) { // Only set if OrderNumber is not already present
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setStatus(OrderStatus.NEW);
            Optional.ofNullable(order.getItems()).ifPresent(it -> it.forEach(item -> item.setOrder(order)));
        }
    }

    static OrderDTO convertToDTO(OrderEntity order) {
        Set<OrderItem> orderItems = order.getItems().stream()
                .map(item -> new OrderItem(item.getCode(), item.getName(), item.getPrice(), item.getQuantity()))
                .collect(Collectors.toSet());

        return new OrderDTO(
                order.getOrderNumber(),
                order.getUserName(),
                orderItems,
                order.getCustomer(),
                order.getDeliveryAddress(),
                order.getStatus(),
                order.getComments(),
                order.getCreatedAt());
    }
}
