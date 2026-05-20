package io.gl4dius.cli.service;

import io.gl4dius.cli.command.SessionManagementCommands;
import io.gl4dius.cli.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:target/session-service-it-${random.uuid}.db",
        "spring.shell.interactive.enabled=false"
})
class SessionManagementServiceIT {

    @Autowired
    private SessionManagementService sessionManagementService;

    @Autowired
    private SessionManagementCommands sessionManagementCommands;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void cleanDatabase() {
        sessionRepository.deleteAll();
    }

    @Test
    void whenCreatesListsAndGetsAndDeletesSession_thenSucceeds() {
        var created = sessionManagementService.createSession("demo", "integration session");

        assertThat(created.getId()).isNotNull();
        assertThat(created.getCreatedAt()).isNotNull();

        var listed = sessionManagementService.listSessions();
        assertThat(listed).hasSize(1);
        assertThat(listed.getFirst().getName()).isEqualTo("demo");

        var byName = sessionManagementService.getSession("demo");
        var byId = sessionManagementService.getSession(created.getId().toString());
        assertThat(byName.getId()).isEqualTo(created.getId());
        assertThat(byId.getName()).isEqualTo("demo");

        var getOutput = sessionManagementCommands.getSession("demo");
        var listOutput = sessionManagementCommands.listSessions();
        assertThat(getOutput).contains("Name: demo", "Mode: PHISHING");
        assertThat(listOutput).contains(created.getId().toString(), "integration session");

        var deleted = sessionManagementService.deleteSession("demo");
        assertThat(deleted.getId()).isEqualTo(created.getId());
        assertThat(sessionManagementService.listSessions()).isEmpty();
        assertThatThrownBy(() -> sessionManagementService.getSession("demo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Session demo not found");
    }

    @Test
    void whenCreatesSessionWithoutName_thenUsesIdAsName() {
        var created = sessionManagementService.createSession(null, null);

        assertThat(created.getName()).isEqualTo(created.getId().toString());
        assertThat(sessionManagementService.getSession(created.getId().toString()).getName()).isEqualTo(created.getName());
    }

    /*@TestConfiguration
    static class ShellRunnerTestConfiguration {

        @Bean
        ApplicationRunner springShellApplicationRunner() {
            return args -> {
            };
        }
    }*/
}
