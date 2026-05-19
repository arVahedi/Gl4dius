package io.gl4dius.cli.configuration;

import io.gl4dius.cli.Gl4diusApplication;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.nio.file.Path;

/**
 * Resolves runtime database file paths relative to the application artifact directory.
 *
 * <p>This utility uses Spring Boot's {@link ApplicationHome} to determine the directory
 * from which the {@link Gl4diusApplication} artifact is running, then resolves the given
 * database file name against that directory.</p>
 *
 * <p>For example, when the application is started from:</p>
 *
 * <pre>{@code
 * /app/gl4dius-cli.jar
 * }</pre>
 *
 * <p>then resolving {@code gl4dius.db} returns:</p>
 *
 * <pre>{@code
 * /app/gl4dius.db
 * }</pre>
 *
 * <p>The returned path is absolute and normalized.</p>
 *
 * <p>This class is not instantiable.</p>
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
final class RuntimeDatabasePathResolver {

    /**
     * Resolves the given database file name against the runtime artifact directory.
     *
     * <p>The artifact directory is derived from {@link ApplicationHome} using
     * {@link Gl4diusApplication} as the application source class. The resulting path is
     * converted to an absolute, normalized path before being returned.</p>
     *
     * @param databaseFileName the database file name to resolve, for example {@code .gl4dius.db};
     *                         must not be {@code null}
     * @return the absolute normalized path to the database file
     */
    static @NonNull Path resolve(String databaseFileName) {
        File applicationHome = new ApplicationHome(Gl4diusApplication.class).getDir();
        Path artifactDirectory = applicationHome.toPath();
        return artifactDirectory.resolve(databaseFileName).toAbsolutePath().normalize();
    }
}
