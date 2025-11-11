package com.louly.soft.bookstore.catalog;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * enable ConfigurationProperties on top of main class
 * and need to configure the same variable pageSize in application properties or yaml file
 * @param pageSize
 */
@ConfigurationProperties(prefix = "catalog")
public record ApplicationProperties(@DefaultValue("10") @Min(1) int pageSize) {}
