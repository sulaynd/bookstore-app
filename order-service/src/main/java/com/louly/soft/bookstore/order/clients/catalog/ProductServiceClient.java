package com.louly.soft.bookstore.order.clients.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductServiceClient {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceClient.class);

    private final RestClient restClient;

    ProductServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * First implementation without Resilience4j
     * @param code
     * @return
     */
    //    public Optional<Product> getProductByCode(String code) {
    //        log.info("Fetching product for code: {}", code);
    //        try {
    //            var product =
    //                    restClient.get()
    //                            .uri("/api/products/{code}", code)
    //                            .retrieve()
    //                            .body(Product.class);
    //
    //            return Optional.ofNullable(product);
    //        }catch (Exception e) {
    //            log.info("Error fetching product for code:{}, Error: {} ", code, e.getMessage());
    //            return Optional.empty();
    //        }
    //    }

    /**
     * Second Implementation with Resilience4j and Timeout/TimeLimiter
     *  When applying Timeout or Time limiter, it's better to handle exceptions here and return Optional.empty()
     *  Instead of returning Optional<Product> we need to return CompletableFuture<String>
     *  So for apply Timeout/TimeLimiter we prefer simply put those Timeout at RestClient level (CatalogServiceClientConfig.java)
     *  where we are registering our RestClient.
     * @param code
     * @return
     */

    /**
     * Implementation with Resilience4j CircuitBreaker and Retry
     * From @Reytry perspective, its not failing, the method executed normally even though the catalog-service is down
     * So we should not be handling exceptions here and return Optional.empty()
     *  If the method fails it will throw exception and retry will handle it.
     *
     * With retry pattern, if the first call fails, it will retry the call based on the configuration
     * with retry pattern, it's better to have a fallback method to handle the failure after all retries are exhausted
     * With retry pattern, it's not better to handle exceptions here and return Optional.empty()
     * If it fails it will throw exception and retry will handle it.
     * If it fails even after retries, it will go to the fallback method.
     * if it fails retry will transfer the control to fallback method.
     * Apply a default fallback method for Retry for if still fails after 3 times
     * The fallback method should have the same return type as the original method and accept the same parameters plus a Throwable
     * And then testing the retry :
     * - First, make sure the catalog-service is running.
     * - Start the order-service.
     * - Stop or sleep the catalog-service to simulate a failure.
     * - Send a request to the order-service to fetch a product by code.
     * - Observe the logs of the order-service to see the retry attempts.
     * - Restart the catalog-service before the retries are exhausted to see a successful response.
     * - If the catalog-service remains down after all retries, observe the fallback method being invoked.
     * - You can adjust the retry configurations such as the number of attempts and wait duration in the application.yml file of the order-service.
     * - You can also test different scenarios by changing the availability of the catalog-service during the retries.
     * Then we can customize the retry configurations in application.yml instead of 3 retries here.
     *
     * And finally add the circuit breaker annotation on top of retry annotation
     * And the test the circuit breaker:
     * - Start the catalog-service and order-service.
     * - Stop or make the catalog-service unresponsive to simulate failures.
     * - Send multiple requests to the order-service to fetch products by code.
     * - Observe the logs of the order-service to see the circuit breaker state transitions (CLOSED, OPEN, HALF_OPEN).
     * - After the failure threshold is reached, the circuit breaker should open, and subsequent calls should go directly to the fallback method.
     * - Restart the catalog-service to allow it to recover.
     * - Observe the circuit breaker transitioning to HALF_OPEN and then to CLOSED after successful calls.
     * - You can adjust the circuit breaker configurations such as failure rate threshold, wait duration, and ring buffer sizes in the application.yml file of the order-service.
     * - Test different scenarios by changing the availability of the catalog-service and observing how the circuit breaker responds.
     *
     * In our case Circuit Breaker will be applied before Retry as per Resilience4j order of precedence.
     * And will aplly Circuit Breaker first, if the circuit is closed it will go to Retry.
     * If the circuit is open it will go to fallback method directly.
     * The fallback method will be applied only for Retry here.
     *
     * So now if we try to run the Tests for the OrderControllerTest.java it will fail because we are trying to call
     * the catalog-service which is not running during the tests.
     * So how do we handle this scenario ?
     * We can use Mocking to mock the ProductServiceClient class and return a dummy product.
     * We can use Mockito for mocking.
     * This way we can test the OrderController without actually calling the catalog-service.
     * So there some tools like WireMock that can help us to mock the external services.
     * We can setup a WireMock server to simulate the catalog-service responses during our tests.
     * This way we can test the resilience features like Circuit Breaker and Retry without needing the actual catalog-service to be up and running.
     * This approach allows us to have more control over the responses and test various scenarios effectively.
     * Specifically we are going to use wiremock-testcontainers library to spin up a WireMock server in a Docker container during our tests.
     * with the wiremock-standalone and wiremock-testcontainers-module(start the wiremock server in the container) dependencies added to our pom.xml.
     * There are two ways we can start the WireMock server using Testcontainers:
     * 1. Using @Testcontainers and @Container annotations to manage the lifecycle of the
     * WireMock container automatically.
     * Run the wiremock server in a container only when running the tests.
     * Add it the AbstractIT.java so that all the test classes can use it.
     * Define a wiremock container by defining a static wiremock Container instance.
     * 2. Manually starting and stopping the WireMock container in the setup and teardown methods of our test class.
     * We will use the first approach for better readability and maintainability.
     * Make sure to configure the RestClient in the OrderService to point to the WireMock server's URL during tests.
     * This can be done by overriding the base URL in the test configuration.
     * Once the WireMock server is set up and running, we can define stub mappings to simulate various responses from the catalog-service.
     * This includes successful responses, failures, and timeouts to thoroughly test the resilience features.
     * By using WireMock with Testcontainers, we can create a controlled testing environment that allows us to validate the behavior of our OrderService under different conditions without relying on the actual catalog-service.
     *
     *
     * @param code
     * @return
     */
    @CircuitBreaker(name = "catalog-service")
    @Retry(name = "catalog-service", fallbackMethod = "getProductByCodeFallback")
    public Optional<Product> getProductByCode(String code) {
        log.info("Fetching product for code: {}", code);
        var product =
                restClient.get().uri("/api/products/{code}", code).retrieve().body(Product.class);
        return Optional.ofNullable(product);
    }

    Optional<Product> getProductByCodeFallback(String code, Throwable t) {
        log.info("catalog-service get product by code fallback: code:{}, Error: {} ", code, t.getMessage());
        return Optional.empty();
    }
}
