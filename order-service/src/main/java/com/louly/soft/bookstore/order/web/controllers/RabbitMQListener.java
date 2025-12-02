// package com.louly.soft.bookstore.order.web.controllers;
//
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.stereotype.Component;
//
// @Component
// public class RabbitMQListener {
//
//    @RabbitListener(queues = "${orders.new-orders-queue}")
//    public void handlerNewOrder(MyPayload myPayload) {
//        System.out.println("New order: " + myPayload.content());
//    }
//
//    @RabbitListener(queues = "${orders.delivered-orders-queue}")
//    public void handlerDeliveredOrder(MyPayload myPayload) {
//        System.out.println("Delivered order: " + myPayload.content());
//    }
// }
