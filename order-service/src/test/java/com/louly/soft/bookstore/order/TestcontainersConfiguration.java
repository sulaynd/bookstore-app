package com.louly.soft.bookstore.order;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.3.0";
    // static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:24.0.2";
    static String realmImportFile = "/bookstore-realm.json";
    static String realmName = "bookstore";

    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.5.2-alpine");

    @Bean
    WireMockContainer wiremockServer() {
        wiremockServer.start();
        configureFor(wiremockServer.getHost(), wiremockServer.getPort());
        return wiremockServer;
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));
    }

    @Bean
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.12.11-alpine"));
    }

    @Bean
    KeycloakContainer keycloak() {
        return new KeycloakContainer(KEYCLOAK_IMAGE).withRealmImportFile(realmImportFile);
    }

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar(WireMockContainer wiremockServer, KeycloakContainer keycloak) {
        return (registry) -> {
            registry.add("orders.catalog-service-url", wiremockServer::getBaseUrl);
            registry.add(
                    "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                    () -> keycloak.getAuthServerUrl() + "/realms/" + realmName);
        };
    }
    //    @Bean
    //    KeycloakContainer keycloak(DynamicPropertyRegistry registry) {
    //        var keycloak = new KeycloakContainer(KEYCLOAK_IMAGE).withRealmImportFile(realmImportFile);
    //       // return (registry) -> {
    //           // registry.add("orders.catalog-service-url", wiremockServer::getBaseUrl);
    //            registry.add(
    //                    "spring.security.oauth2.resourceserver.jwt.issuer-uri",
    //                    () -> keycloak.getAuthServerUrl() + "/realms/" + realmName);
    //        //};
    //
    //        return keycloak;
    //    }
}
