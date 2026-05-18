package io.gl4dius.cli.configuration;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RuntimeDatabasePropertiesPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "gl4diusRuntimeDatabase";
    private static final String DEFAULT_DATABASE_FILE_NAME = ".gl4dius.db";

    @Override
    public void postProcessEnvironment(@NonNull ConfigurableEnvironment environment, @NonNull SpringApplication application) {
        Map<String, Object> properties = new LinkedHashMap<>();
        String databaseFileName = environment.getProperty("gl4dius.database.file-name", DEFAULT_DATABASE_FILE_NAME);

        if (!environment.containsProperty("spring.datasource.url")) {
            Path databasePath = RuntimeDatabasePathResolver.resolve(databaseFileName);
            createDirectory(databasePath.getParent());
            properties.put("spring.datasource.url", "jdbc:sqlite:" + databasePath);
        }

        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void createDirectory(Path directory) {
        if (directory == null) {
            return;
        }
        try {
            Files.createDirectories(directory);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create SQLite database directory " + directory, ex);
        }
    }
}
