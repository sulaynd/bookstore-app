package com.louly.soft.bookstore.order.domain;

import com.louly.soft.bookstore.order.ApplicationProperties;
import com.louly.soft.bookstore.order.domain.models.OrderCancelledEvent;
import com.louly.soft.bookstore.order.domain.models.OrderCreatedEvent;
import com.louly.soft.bookstore.order.domain.models.OrderDeliveredEvent;
import com.louly.soft.bookstore.order.domain.models.OrderErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
class OrderEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderEventService.class);
    private final RabbitTemplate rabbitTemplate;
    private final ApplicationProperties properties;

    OrderEventPublisher(RabbitTemplate rabbitTemplate, ApplicationProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    public void publish(OrderCreatedEvent event) {
        log.info("publish Event with event : {}", event);
        send(properties.newOrdersQueue(), event);
    }

    public void publish(OrderDeliveredEvent event) {
        this.send(properties.deliveredOrdersQueue(), event);
    }

    public void publish(OrderCancelledEvent event) {
        this.send(properties.cancelledOrdersQueue(), event);
    }

    public void publish(OrderErrorEvent event) {
        this.send(properties.errorOrdersQueue(), event);
    }

    private void send(String routingKey, Object payload) {
        log.info("Send Event routingKey {}. and payload: {}", routingKey, payload);
        rabbitTemplate.convertAndSend(properties.orderEventsExchange(), routingKey, payload);
    }
}
