package com.louly.soft.bookstore.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
// @NoArgsConstructor
// @AllArgsConstructor
class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_generator")
    @SequenceGenerator(name = "product_id_generator", sequenceName = "product_id_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Product code is required") private String code;

    @NotBlank(message = "Product name is required") @Column(nullable = false)
    private String name;

    private String description;

    private String imageUrl;

    @NotNull(message = "Product price is required") @DecimalMin("0.1") @Column(nullable = false)
    private BigDecimal price;
}
