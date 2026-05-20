package io.gl4dius.cli;

import io.gl4dius.cli.model.entity.Session;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class Gl4diusApplication {

    @Setter
    private static Session currentSession;

    public static void main(String[] args) {
        SpringApplication.run(Gl4diusApplication.class, args);
    }

    public static @NonNull Optional<Session> getCurrentSession() {
        return Optional.ofNullable(currentSession);
    }
}
