package io.gl4dius.core;

import io.gl4dius.cli.Gl4diusApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = Gl4diusApplication.class,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.shell.core.autoconfigure.SpringShellAutoConfiguration"
        }
)
class Gl4diusApplicationTests {

    @Test
    void contextLoads() {
    }

}
