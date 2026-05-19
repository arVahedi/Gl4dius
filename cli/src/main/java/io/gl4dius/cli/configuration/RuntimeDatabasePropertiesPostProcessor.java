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

/**
 * Adds a default runtime SQLite datasource URL when no explicit datasource URL
 * has been configured.
 *
 * <p>This post-processor runs during Spring Boot environment preparation. It checks
 * whether {@code spring.datasource.url} is already present. If it is missing, it
 * resolves an SQLite database path and contributes the following property:</p>
 *
 * <pre>{@code
 * spring.datasource.url=jdbc:sqlite:<resolved-database-path>
 * }</pre>
 *
 * <p>The database file name can be customized with:</p>
 *
 * <pre>{@code
 * gl4dius.database.file-name=my-database.db
 * }</pre>
 *
 * <p>If no custom file name is configured, {@code .gl4dius.db} is used.</p>
 *
 * <p>The database parent directory is created automatically before the datasource
 * URL is added to the environment.</p>
 *
 * <p>This post-processor uses {@link Ordered#LOWEST_PRECEDENCE}, so explicitly
 * configured application properties should take priority over this default value.</p>
 */
public final class RuntimeDatabasePropertiesPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "gl4diusRuntimeDatabase";
    private static final String DEFAULT_DATABASE_FILE_NAME = ".gl4dius.db";

    /**
     * Adds a default SQLite datasource URL to the Spring environment when no
     * datasource URL has been configured.
     *
     * <p>If {@code spring.datasource.url} already exists, this method leaves the
     * datasource configuration untouched. Otherwise, it resolves the runtime database
     * path from {@code gl4dius.database.file-name}, creates the parent directory if
     * necessary, and adds a low-priority property source containing the generated
     * SQLite JDBC URL.</p>
     *
     * @param environment the configurable Spring environment to inspect and update;
     *                    must not be {@code null}
     * @param application the current Spring application; must not be {@code null}
     */
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

    /**
     * Returns the execution order for this environment post-processor.
     *
     * <p>The lowest precedence is used so that this post-processor behaves as a
     * fallback provider and does not override explicitly configured datasource
     * properties.</p>
     *
     * @return {@link Ordered#LOWEST_PRECEDENCE}
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * Creates the given directory and any missing parent directories.
     *
     * <p>If the given directory is {@code null}, this method does nothing. Directory
     * creation failures are wrapped in an {@link IllegalStateException} so startup
     * fails fast with a clear message.</p>
     *
     * @param directory the directory to create; may be {@code null}
     * @throws IllegalStateException if the directory cannot be created
     */
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
