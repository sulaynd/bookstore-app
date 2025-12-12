package com.louly.soft.webapp.clients.orders;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface OrderServiceClient {
    @PostExchange("/orders/api/orders")
    // OrderConfirmationDTO createOrder(@RequestHeader Map<String, ?> headers, @RequestBody CreateOrderRequest
    // orderRequest);
    OrderConfirmationDTO createOrder(@RequestBody CreateOrderRequest orderRequest);

    @GetExchange("/orders/api/orders")
    // List<OrderSummary> getOrders(@RequestHeader Map<String, ?> headers);
    List<OrderSummary> getOrders();

    @GetExchange("/orders/api/orders/{orderNumber}")
    // OrderDTO getOrder(@RequestHeader Map<String, ?> headers, @PathVariable String orderNumber);
    OrderDTO getOrder(@PathVariable String orderNumber);
}
