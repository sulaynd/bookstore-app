package com.louly.soft.bookstore.order.web.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.louly.soft.bookstore.order.AbstractIT;
import com.louly.soft.bookstore.order.domain.models.OrderSummary;
import com.louly.soft.bookstore.order.testdata.TestDataFactory;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

/**
 * JUnit5 features is we can write nested test classes to group tests
 * To organize Tests in nested classes.
 * It's not mandatory but it helps in better organization of tests.
 *
 * For every tests method it will run the test-orders sql file script
 */
@Sql("/test-orders.sql")
class OrderControllerTest extends AbstractIT {

    @Nested
    class CreateOrderTests {
        @Test
        void shouldCreateOrderSuccessfully() {

            mockGetProductByCode("P100", "Mouse Logitech", new BigDecimal("25.50"));

            var payload =
                    """
                        {
                            "customer" : {
                                "name": "John",
                                "email": "john@gmail.com",
                                "phone": "999999999"
                            },
                            "deliveryAddress" : {
                                "addressLine1": "616 rue des melezes",
                                "addressLine2": "sainte foy",
                                "city": "Quebec",
                                "state": "Quebec",
                                "zipCode": "G1X3C5",
                                "country": "Canada"
                            },
                            "items": [
                                {
                                    "code": "P100",
                                    "name": "Mouse Logitech",
                                    "price": 25.50,
                                    "quantity": 1
                                }
                            ]
                        }
                    """;

            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + getToken())
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("orderNumber", notNullValue());
        }

        @Test
        void shouldReturnBadRequestWhenMandatoryDataIsMissing() {
            var payload = TestDataFactory.createOrderRequestWithInvalidCustomer();
            given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + getToken())
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class GetOrdersTests {
        String orderNumber = "order-123";

        @Test
        void shouldGetOrdersSuccessfully() {
            List<OrderSummary> orderSummaries = given().when()
                    .header("Authorization", "Bearer " + getToken())
                    .get("/api/orders")
                    .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(new TypeRef<>() {});

            assertThat(orderSummaries).hasSize(2);
        }

        @Test
        void shouldGetOrderByOrderNumberSuccessfully() {
            given().when()
                    .header("Authorization", "Bearer " + getToken())
                    .get("/api/orders/{orderNumber}", orderNumber)
                    .then()
                    .statusCode(200)
                    .body("orderNumber", is(orderNumber))
                    .body("items.size()", is(2));
        }
    }
}
