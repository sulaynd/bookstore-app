package com.louly.soft.bookstore.order.clients.catalog;

import com.louly.soft.bookstore.order.ApplicationProperties;
import java.time.Duration;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
class CatalogServiceClientConfig {

    /**
     * First implementation of RestClient without timeouts
     * @param properties
     * @return
     */
    //    @Bean
    //    RestClient restClient(ApplicationProperties properties) {
    //        return RestClient.builder()
    //                .baseUrl(properties.catalogServiceUrl())
    //                .build();
    //    }

    /**
     * Second implementation of RestClient with timeouts
     *
     */
    @Bean
    RestClient restClient(RestClient.Builder builder, ApplicationProperties properties) {
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder.simple()
                .withCustomizer(customizer -> {
                    customizer.setConnectTimeout(
                            Duration.ofSeconds(5)); // try first to establish connection to the target endpoint API
                    customizer.setReadTimeout(
                            Duration.ofSeconds(5)); // waiting for 5 second to receive the response data
                })
                .build();
        return builder.baseUrl(properties.catalogServiceUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
