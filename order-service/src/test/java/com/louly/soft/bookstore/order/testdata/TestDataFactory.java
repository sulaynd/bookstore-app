package com.louly.soft.bookstore.order.testdata;

import static org.instancio.Select.field;

import com.louly.soft.bookstore.order.domain.models.Address;
import com.louly.soft.bookstore.order.domain.models.Customer;
import com.louly.soft.bookstore.order.domain.models.OrderDTO;
import com.louly.soft.bookstore.order.domain.models.OrderItem;
import com.louly.soft.bookstore.order.domain.models.OrderRequest;
import com.louly.soft.bookstore.order.domain.models.OrderStatus;
import com.louly.soft.bookstore.order.domain.models.OrderSummary;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.instancio.Instancio;

public class TestDataFactory {
    static final List<String> VALID_COUNTIES = List.of("Canada", "Germany");
    static final Set<OrderItem> VALID_ORDER_ITEMS =
            Set.of(new OrderItem("P100", "Product 1", new BigDecimal("25.50"), 1));
    static final Set<OrderItem> INVALID_ORDER_ITEMS =
            Set.of(new OrderItem("ABCD", "Product 1", new BigDecimal("25.50"), 1));

    public static OrderRequest createValidOrderRequest() {
        return Instancio.of(OrderRequest.class)
                .generate(field(Customer::email), gen -> gen.text().pattern("#a#a#a#a#a#a@mail.com"))
                .set(field(OrderRequest::items), VALID_ORDER_ITEMS)
                .generate(field(Address::country), gen -> gen.oneOf(VALID_COUNTIES))
                .create();
    }

    public static OrderRequest createOrderRequestWithInvalidCustomer() {
        return Instancio.of(OrderRequest.class)
                .generate(field(Customer::email), gen -> gen.text().pattern("#c#c#c#c#d#d@mail.com"))
                .set(field(Customer::phone), "")
                .generate(field(Address::country), gen -> gen.oneOf(VALID_COUNTIES))
                .set(field(OrderRequest::items), VALID_ORDER_ITEMS)
                .create();
    }

    public static OrderRequest createOrderRequestWithInvalidDeliveryAddress() {
        return Instancio.of(OrderRequest.class)
                .generate(field(Customer::email), gen -> gen.text().pattern("#c#c#c#c#d#d@mail.com"))
                .set(field(Address::country), "")
                .set(field(OrderRequest::items), VALID_ORDER_ITEMS)
                .create();
    }

    public static OrderRequest createOrderRequestWithNoItems() {
        return Instancio.of(OrderRequest.class)
                .generate(field(Customer::email), gen -> gen.text().pattern("#c#c#c#c#d#d@mail.com"))
                .generate(field(Address::country), gen -> gen.oneOf(VALID_COUNTIES))
                .set(field(OrderRequest::items), Set.of())
                .create();
    }

    public static List<OrderSummary> getOrderSummaries() {
        List<OrderSummary> orderSummaries = new ArrayList<>();
        // Example: Create OrderSummary objects and add them to the list
        OrderSummary order1 = new OrderSummary("order-123", OrderStatus.DELIVERED);
        OrderSummary order2 = new OrderSummary("order-456", OrderStatus.CANCELLED);
        orderSummaries.add(order1);
        orderSummaries.add(order2);

        return orderSummaries;
    }

    public static Optional<OrderDTO> getOrder() {
        Set<OrderItem> items = new HashSet<>();
        Customer customer = new Customer("Mao", "mao@gmail.com", "999999999");
        Address address = new Address("202-594 avenue de norvege", null, "Quebec city", "Quebec", "G1X 3E8", "INDIA");
        OrderItem orderItem = new OrderItem("P100", "Mac Mouse", BigDecimal.valueOf(34.00), 1);
        items.add(orderItem);
        return Optional.of(new OrderDTO(
                "290941ba-6bfb-446c-9930-b476fad0480c",
                "user",
                items,
                customer,
                address,
                OrderStatus.DELIVERED,
                null,
                LocalDateTime.now()));
    }
}
