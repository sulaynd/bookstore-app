package com.louly.soft.bookstore.catalog.web.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.louly.soft.bookstore.catalog.AbstractIT;
import com.louly.soft.bookstore.catalog.domain.Product;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@Sql("/test-data.sql")
class ProductControllerTest extends AbstractIT {

    @Test
    void shouldReturnProducts() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("data", hasSize(10))
                .body("totalElements", is(15))
                .body("pageNumber", is(1))
                .body("totalPages", is(2))
                .body("isFirst", is(true))
                .body("isLast", is(false))
                .body("hasNext", is(true))
                .body("hasPrevious", is(false));
    }

        @Test
        void shouldGetProductByCode() {
            String code = "P100";
            given().contentType(ContentType.JSON)
                    .when()
                    .get("/api/products/{code}", code)
                    .then()
                    .statusCode(200)
                    .body("code", is("P100"))
                    .body("name", is("The Hunger Games"))
                    .body("description", is("Winning will make you famous. Losing means certain death..."))
                    .body("price", is(34.0f));
        }

//    @Test
//    void shouldGetProductByCode() {
//        Product product = given().contentType(ContentType.JSON)
//                .when()
//                .get("/api/products/{code}", "P100")
//                .then()
//                .statusCode(200)
//                .assertThat()
//                .extract()
//                .body()
//                .as(Product.class);
//
//        assertThat(product.code()).isEqualTo("P100");
//        assertThat(product.name()).isEqualTo("The Hunger Games");
//        assertThat(product.description()).isEqualTo("Winning will make you famous. Losing means certain death...");
//        assertThat(product.price()).isEqualTo(new BigDecimal("34.0"));
//    }

    @Test
    void shouldReturnNotFoundWhenProductCodeNotExists() {
        String code = "invalid_product_code";
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", code)
                .then()
                .statusCode(404)
                .body("status", is(404))
                .body("title", is("Product Not Found"))
                .body("detail", is("Product with code " + code + " not found"));
    }

    //    @Test
    //    void shouldReturnNotFoundWhenProductCodeNotExists() {
    //        String code = "invalid_product_code";
    //        given().contentType(ContentType.JSON)
    //                .when()
    //                .get("/api/products/{code}", code)
    //                .then()
    //                .statusCode(404);
    //    }

    //    @Test
    //    void shouldReturnProductsWithDefaultPagination() {
    //        given().contentType(ContentType.JSON)
    //                .when()
    //                .get("/api/products")
    //                .then()
    //                .statusCode(200)
    //                .body("data", hasSize(10))
    //                .body("pageNumber", is(1))
    //                .body("totalPages", is(2));
    //    }
    //
    //    @Test
    //    void shouldReturnProductsWithCustomPagination() {
    //        given().contentType(ContentType.JSON)
    //                .when()
    //                .get("/api/products?page=2&size=5")
    //                .then()
    //                .statusCode(200)
    //                .body("data", hasSize(5))
    //                .body("pageNumber", is(2))
    //                .body("totalPages", is(2));
    //    }
    //
    //    @Test
    //    void shouldReturnEmptyProductsWhenPageNumberExceedsTotalPages() {
    //        given().contentType(ContentType.JSON)
    //                .when()
    //                .get("/api/products?page=3&size=5")
    //                .then()
    //                .statusCode(200)
    //                .body("data", hasSize(0))
    //                .body("pageNumber", is(3))
    //                .body("totalPages", is(2));
    //    }
}
