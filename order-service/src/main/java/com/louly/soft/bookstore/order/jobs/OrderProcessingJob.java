package com.louly.soft.bookstore.order.jobs;

import com.louly.soft.bookstore.order.domain.OrderService;
import java.time.Instant;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class OrderProcessingJob {
    private static final Logger log = LoggerFactory.getLogger(OrderProcessingJob.class);

    private final OrderService orderService;

    OrderProcessingJob(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(cron = "${orders.new-orders-job-cron}")
    @SchedulerLock(name = "processNewOrders")
    public void processNewOrders() {
        /**
         * Check if we are able to acquire the lock before proceeding with the job execution.
         * This is useful for debugging and ensuring that the distributed lock
         * mechanism is functioning correctly.
         *
         * To assert that the lock is held (prevents misconfiguration errors)
         */
        //  LockAssert.assertLocked();
        log.info("Processing new orders at {}", Instant.now());
        orderService.processNewOrders();
    }
}
