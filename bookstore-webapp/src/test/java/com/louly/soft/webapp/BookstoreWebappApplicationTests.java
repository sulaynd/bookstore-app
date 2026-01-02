package com.louly.soft.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
class BookstoreWebappApplicationTests {

    @Test
    @WithMockUser
    void contextLoads() {}
}
