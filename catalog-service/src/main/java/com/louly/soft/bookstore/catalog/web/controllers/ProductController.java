package com.louly.soft.bookstore.catalog.web.controllers;

import com.louly.soft.bookstore.catalog.domain.PagedResult;
import com.louly.soft.bookstore.catalog.domain.Product;
import com.louly.soft.bookstore.catalog.domain.ProductNotFoundException;
import com.louly.soft.bookstore.catalog.domain.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @GetMapping
    PagedResult<Product> getProducts(@RequestParam(name = "page", defaultValue = "1") int pageNo) {
        log.info("Fetching products for page: {}", pageNo);
        /**
         * Timeout testing
         * This is for demonstration purposes only.
         * sleep();
         */
        return productService.getProducts(pageNo);
    }

    @GetMapping("/{code}")
    ResponseEntity<Product> getProductByCode(@PathVariable String code) {
        log.info("Fetching product by code: {}", code);
        /**
         * Timeout testing
         * This is for demonstration purposes only.
         * sleep();
         */
        // sleep();
        return productService
                .getProductByCode(code)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ProductNotFoundException.forCode(code));
    }

    /**
     * For testing resilience4j timeouts
     * This is for demonstration purposes only.
     */
    void sleep() {
        try {
            Thread.sleep(6000); // waiting for 6 seconds since timeout is for 5 seconds
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
