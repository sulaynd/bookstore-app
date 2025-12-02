package com.louly.soft.bookstore.order.jobs;

import com.louly.soft.bookstore.order.domain.OrderEventService;
import java.time.Instant;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class OrderEventsPublishingJob {
    private static final Logger log = LoggerFactory.getLogger(OrderEventsPublishingJob.class);

    private final OrderEventService orderEventService;

    OrderEventsPublishingJob(OrderEventService orderEventService) {
        this.orderEventService = orderEventService;
    }

    /**
     * Implemented the distributed locking for our Jon scheduler? Why we need this and how to implement it?
     * What is the problem we are trying to solve here?
     * In our Order-service we have implemented two scheduled jobs to process whereas tasks:
     * Mostly likely in a production environment we will have multiple instances
     * of our microservice running for high availability and load balancing.
     * If we don't have a distributed locking mechanism in place,
     * each instance of the microservice will try to execute the scheduled job independently.
     * This can lead to several issues such as:
     * 1. Duplicate Processing: Multiple instances may process the same data simultaneously,
     * leading to duplicate entries or actions.
     * 2. Data Inconsistency: Concurrent modifications to the same data can result in
     * data corruption or inconsistency.
     * 3. Increased Load: All instances running the same job can put unnecessary load on
     * the database or other resources, potentially degrading performance.
     * To solve this problem we have used ShedLock library which provides a simple way to
     * implement distributed locks for scheduled tasks in a Spring Boot application.
     * It ensures that only one instance of the application can execute a particular
     * scheduled job at any given time across a distributed system.
     * Here is how we have implemented it:
     * 1. Add Dependency: First, we need to add the ShedLock dependency to
     * our project's build file (Maven or Gradle).
     * 2. Configure Lock Provider: We need to configure a lock provider that
     * uses our database to store lock information. ShedLock supports various databases.
     * 3. Annotate Scheduled Methods: We annotate our scheduled job methods with
     * @SchedulerLock annotation, providing a unique name for the lock.
     * This ensures that when the scheduled method is invoked,
     * ShedLock will attempt to acquire the lock before executing the method.
     * If the lock is already held by another instance, the method will not be executed.
     * 4. Configure Lock Duration: We can configure the lock duration to specify
     * how long the lock should be held. This helps to prevent deadlocks in case
     * an instance crashes while holding the lock.
     * By implementing distributed locking using ShedLock, we can ensure that
     * our scheduled jobs are executed safely and consistently across multiple
     * instances of our microservice, preventing issues related to duplicate processing,
     * data inconsistency, and increased load.
     *
     *
     *
     */
    @Scheduled(cron = "${orders.publish-order-events-job-cron}")
    @SchedulerLock(name = "publishOrderEvents")
    public void publishOrderEvents() {
        /**
         * Check if we are able to acquire the lock before proceeding with the job execution.
         * This is useful for debugging and ensuring that the distributed lock
         * mechanism is functioning correctly.
         *
         * To assert that the lock is held (prevents misconfiguration errors)
         */
        //  LockAssert.assertLocked();
        log.info("Publishing Order Events at {}", Instant.now());
        orderEventService.publishOrderEvents();
    }
}
