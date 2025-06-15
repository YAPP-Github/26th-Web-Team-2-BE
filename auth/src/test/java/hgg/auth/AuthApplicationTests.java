package hgg.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(
        classes = AuthApplicationTests.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class AuthApplicationTests {

    @Configuration
    static class TestConfig {}

    @Test
    void contextLoads() {
    }

}
