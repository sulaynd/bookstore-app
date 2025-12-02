package com.louly.soft.bookstore.order.domain;

import com.louly.soft.bookstore.order.domain.models.OrderCreatedEvent;
import com.louly.soft.bookstore.order.domain.models.OrderDTO;
import com.louly.soft.bookstore.order.domain.models.OrderErrorEvent;
import com.louly.soft.bookstore.order.domain.models.OrderRequest;
import com.louly.soft.bookstore.order.domain.models.OrderResponse;
import com.louly.soft.bookstore.order.domain.models.OrderStatus;
import com.louly.soft.bookstore.order.domain.models.OrderSummary;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final List<String> DELIVERY_ALLOWED_COUNTRIES = List.of("INDIA", "USA", "CANADA", "GERMANY", "UK");

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderValidator orderValidator;
    private final OrderEventService orderEventService;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public OrderResponse createOrder(String userName, OrderRequest request) {
        orderValidator.validate(request);
        //  OrderEntity newOrder = OrderMapper.convertToEntity(request);
        OrderEntity newOrder = orderMapper.toEntity(request);
        newOrder.setUserName(userName);
        OrderEntity savedOrder = orderRepository.save(newOrder);
        log.info("Created Order with orderNumber={}", savedOrder.getOrderNumber());
        /**
         * We can publish an OrderCreatedEvent here for other microservices to consume
         * but the problem is this is a transactional method (all successfully commit or rollback) and if we publish the event here,
         * there is a chance that the transaction may fail after the event is published.
         * But we send a message to rabbitMQ here and for any reason if the database commit fails
         * and it throws some exception and all changes made in this method will be rolled back,
         * but the message is already sent to rabbitMQ and other microservices may have already processed the event
         * To solve this problem, we can use Outbox pattern or Transactional Event Publishing or Schedule Job
         */
        OrderCreatedEvent orderCreatedEvent = OrderEventMapper.buildOrderCreatedEvent(savedOrder);
        orderEventService.save(orderCreatedEvent);

        return new OrderResponse(savedOrder.getOrderNumber());
    }

    public List<OrderSummary> findOrders(String userName) {
        /**
         * this is bad implementation why?
         *  we are fetching the entire orders information from DB
         *  and then dropping all the information except orderNumber and status
         *  so unnecessary we are loading all the order information for only to use orderNumber and status
         *  using the OrderSummary constructor expression
         *  so we should not do this
         *  in instead of doing derived query we will provide our own query into the order repository
         *  using JPQL and return OrderSummary itself instead of OrderEntity
         *
         */
        //            List<OrderEntity> orders = orderRepository.findByUserName2(userName);
        //            return orders.stream()
        //                    .map(order -> new OrderSummary(order.getOrderNumber(), order.getStatus()))
        //                    .toList();
        return orderRepository.findByUserName(userName);
    }

    public Optional<OrderDTO> findUserOrder(String userName, String orderNumber) {
        return orderRepository
                .findByUserNameAndOrderNumber(userName, orderNumber)
                .map(OrderMapper::convertToDTO);
    }

    public void processNewOrders() {
        List<OrderEntity> orders = orderRepository.findByStatus(OrderStatus.NEW);
        log.info("Found {} new orders to process", orders.size());
        for (OrderEntity order : orders) {
            this.process(order);
        }
    }

    private void process(OrderEntity order) {
        try {
            if (canBeDelivered(order)) {
                log.info("OrderNumber: {} can be delivered", order.getOrderNumber());
                orderRepository.updateOrderStatus(order.getOrderNumber(), OrderStatus.DELIVERED);
                orderEventService.save(OrderEventMapper.buildOrderDeliveredEvent(order));

            } else {
                log.info("OrderNumber: {} can not be delivered", order.getOrderNumber());
                orderRepository.updateOrderStatus(order.getOrderNumber(), OrderStatus.CANCELLED);
                orderEventService.save(OrderEventMapper.buildOrderCancelledEvent(
                        order,
                        "Can't deliver to the location : "
                                + order.getDeliveryAddress().country()));
            }
        } catch (RuntimeException e) {
            log.error("Failed to process Order with orderNumber: {}", order.getOrderNumber(), e);
            orderRepository.updateOrderStatus(order.getOrderNumber(), OrderStatus.ERROR);
            orderEventService.save(OrderEventMapper.buildOrderErrorEvent(order, e.getMessage()));
        }
    }

    private boolean canBeDelivered(OrderEntity order) {
        return DELIVERY_ALLOWED_COUNTRIES.contains(
                order.getDeliveryAddress().country().toUpperCase());
    }

    @RabbitListener(queues = "${orders.cancelled-orders-queue}")
    public void OrderErrorEventListener(OrderErrorEvent errorEvent) {
        log.info("OrderErrorEventListener Received error event: {}", errorEvent.orderNumber());
        // Process the error message as needed

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(errorEvent.customer().email()); // Replace with actual customer email
        message.setFrom(fromEmail); // customers support business email
        message.setSubject("Order Processing Error");
        message.setText("An error occurred while processing order " + errorEvent.orderNumber() + ".\nReason : "
                + errorEvent.reason());

        mailSender.send(message);

        System.out.println("Order error email sent for order: " + errorEvent.orderNumber());
    }
}
