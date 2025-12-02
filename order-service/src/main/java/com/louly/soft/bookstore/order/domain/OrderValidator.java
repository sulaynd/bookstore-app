package com.louly.soft.bookstore.order.domain;

import com.louly.soft.bookstore.order.clients.catalog.Product;
import com.louly.soft.bookstore.order.clients.catalog.ProductServiceClient;
import com.louly.soft.bookstore.order.domain.models.OrderItem;
import com.louly.soft.bookstore.order.domain.models.OrderRequest;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class OrderValidator {
    private static final Logger log = LoggerFactory.getLogger(OrderValidator.class);

    private final ProductServiceClient client;

    OrderValidator(ProductServiceClient client) {
        this.client = client;
    }

    void validate(OrderRequest request) {
        Set<OrderItem> items = request.items();
        // For each item in the order, validate the product code and price
        for (OrderItem item : items) {
            Product product = client.getProductByCode(item.code())
                    .orElseThrow(() -> new InvalidOrderException("Invalid Product code:" + item.code()));
            if (item.price().compareTo(product.price()) != 0) {
                log.error(
                        "Product price not matching. Actual price:{}, received price:{}",
                        product.price(),
                        item.price());
                throw new InvalidOrderException("Product price not matching");
            }
        }
    }
}
