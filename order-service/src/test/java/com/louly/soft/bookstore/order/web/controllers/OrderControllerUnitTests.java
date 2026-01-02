package com.louly.soft.bookstore.order.web.controllers;

import static com.louly.soft.bookstore.order.testdata.TestDataFactory.createOrderRequestWithInvalidCustomer;
import static com.louly.soft.bookstore.order.testdata.TestDataFactory.createOrderRequestWithInvalidDeliveryAddress;
import static com.louly.soft.bookstore.order.testdata.TestDataFactory.createOrderRequestWithNoItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.louly.soft.bookstore.order.domain.OrderService;
import com.louly.soft.bookstore.order.domain.SecurityService;
import com.louly.soft.bookstore.order.domain.models.OrderRequest;
import com.louly.soft.bookstore.order.domain.models.OrderStatus;
import com.louly.soft.bookstore.order.testdata.TestDataFactory;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerUnitTests {
    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private SecurityService securityService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        given(securityService.getLoginUserName()).willReturn("dieg");
    }

    /**
     * Replace @Test with @ParameterizedTest for the method you want to parameterize. Provide arguments using a source annotation.
     * JUnit 5 offers several annotations to supply arguments to your parameterized test:
     * @ValueSource: Provides a single array of primitive values (e.g., strings, ints, doubles, longs).
     *
     * @ValueSource(strings = {"apple", "banana", "orange"})
     * @CsvSource({"apple, 1", "banana, 2", "orange, 3"})
     * @CsvSource: Provides comma-separated values directly within the annotation.
     * @CsvFileSource(resources = "/data.csv", numLinesToSkip = 1) // data.csv in src/test/resources
     * @CsvFileSource: Reads arguments from a CSV file located in your resources folder.
     * @EnumSource(Fruit.class) -> @EnumSource: Provides enum constants as arguments.
     * @MethodSource("fruitAndQuantityProvider")
     * @MethodSource: References a static method within the same class (or another class) that returns a Stream, Iterable, or an array of arguments.
     * @param request
     * @throws Exception
     */
    @ParameterizedTest(name = "[{index}]-{0}")
    @MethodSource("createOrderRequestProvider")
    // setting the mockUser into the security context
    @WithMockUser
    void shouldReturnBadRequestWhenOrderPayloadIsInvalid(OrderRequest request) throws Exception {
        // we can take out the following line if we want also but let leave it here for clarity
        // given(orderService.createOrder(eq("dieg"), any(OrderRequest.class))).willThrow(new
        // InvalidOrderException(null));
        // when(orderService.createOrder(eq("dieg"), any(OrderRequest.class))).thenThrow(new
        // InvalidOrderException(null));
        given(orderService.createOrder(eq("dieg"), any(OrderRequest.class))).willReturn(null);
        mockMvc.perform(post("/api/orders")
                        // adding CSRF token since POST request requires it
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> createOrderRequestProvider() {
        return Stream.of(
                arguments(named("Order with Invalid Customer", createOrderRequestWithInvalidCustomer())),
                arguments(named("Order with Invalid Delivery Address", createOrderRequestWithInvalidDeliveryAddress())),
                arguments(named("Order with No Items", createOrderRequestWithNoItems())));
    }

    @Test
    @WithMockUser
    void shouldGetOrdersSuccessfully() throws Exception {
        var orderSummaries = TestDataFactory.getOrderSummaries();
        // Mock the service behavior
        when(orderService.findOrders(any())).thenReturn(orderSummaries);
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber", is("order-123")))
                .andExpect(jsonPath("$[0].status", is(OrderStatus.DELIVERED.toString())))
                .andExpect(jsonPath("$[1].orderNumber", is("order-456")))
                .andExpect(jsonPath("$[1].status", is(OrderStatus.CANCELLED.toString())));

        // Verify that the service method was called
        verify(orderService, times(1)).findOrders("dieg");
    }

    @Test
    @WithMockUser
    void shouldGetOrderByOrderNumberSuccessfully() throws Exception {
        String orderNumber = "290941ba-6bfb-446c-9930-b476fad0480c";
        var order = TestDataFactory.getOrder();
        when(orderService.findUserOrder(any(), any())).thenReturn(order);
        mockMvc.perform(get("/api/orders/" + orderNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber", is(orderNumber)))
                .andExpect(jsonPath("$.status", is(OrderStatus.DELIVERED.toString())))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @WithMockUser
    void shouldThrowOrderNotFoundException() throws Exception {
        String orderNumber = "290941ba";
        when(orderService.findUserOrder(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/" + orderNumber)).andExpect(status().isNotFound()); // Assert HTTP 404
    }
}
