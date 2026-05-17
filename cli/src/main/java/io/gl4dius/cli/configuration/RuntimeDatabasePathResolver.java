package io.gl4dius.cli.configuration;

import io.gl4dius.cli.Gl4diusApplication;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.nio.file.Path;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
final class RuntimeDatabasePathResolver {

    static @NonNull Path resolve(String databaseFileName) {
        File applicationHome = new ApplicationHome(Gl4diusApplication.class).getDir();
        Path artifactDirectory = applicationHome.toPath();
        return artifactDirectory.resolve(databaseFileName).toAbsolutePath().normalize();
    }
}
