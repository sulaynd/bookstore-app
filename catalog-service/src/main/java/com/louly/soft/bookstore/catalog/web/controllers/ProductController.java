package com.louly.soft.bookstore.catalog.web.controllers;

import com.louly.soft.bookstore.catalog.domain.PagedResult;
import com.louly.soft.bookstore.catalog.domain.Product;
import com.louly.soft.bookstore.catalog.domain.ProductNotFoundException;
import com.louly.soft.bookstore.catalog.domain.ProductService;
import lombok.RequiredArgsConstructor;
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

    private final ProductService productService;

    @GetMapping
    PagedResult<Product> getProducts(@RequestParam(name = "page", defaultValue = "1") int pageNo) {
        return productService.getProducts(pageNo);
    }

    @GetMapping("/{code}")
    ResponseEntity<Product> getProductByCode(@PathVariable String code) {

        return productService
                .getProductByCode(code)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ProductNotFoundException.forCode(code));
    }
}
