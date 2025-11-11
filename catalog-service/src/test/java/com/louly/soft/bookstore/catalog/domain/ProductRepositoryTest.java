package com.louly.soft.bookstore.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * By default DataJpaTest add in memory support database like H2.
 * But we do not want to test with in memory database because we are using postgresql
 * so we should testing with postgresql only by using
 * Testcontainers by using properties for database testing
 *
 */
@DataJpaTest(
        properties = {
            "spring.test.database.replace=none", // do not try to create database testing in memory settings
            "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db", // special database url where tests will talk to
            // this db
            // which only talk to the db itself not with other like rabbitMQ while testing repo does not need rabbitMQ
        })
@Sql("/test-data.sql")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    // You don't need to test the methods provided by Spring Data JPA.
    // This test is to demonstrate how to write tests for the repository layer.
    @Test
    void shouldGetAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        assertThat(products).hasSize(15);
    }

    @Test
    void shouldFindProductByCode() {
        String code = "P100";
        ProductEntity product = productRepository.findByCode(code).orElseThrow();
        assertThat(product.getName()).isEqualTo("The Hunger Games");
        assertThat(product.getDescription()).isEqualTo("Winning will make you famous. Losing means certain death...");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("34.0"));
    }

    @Test
    void shouldReturnEmptyWhenProductCodeNotExists() {
        String code = "invalid_product_code";
        var productOpt = productRepository.findByCode(code);
        assertThat(productOpt).isEmpty();
    }
}
