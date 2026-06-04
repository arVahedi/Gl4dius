package io.gl4dius.cli.model.dto;

import java.nio.file.Path;

public record StaticResource(
        Path path,
        long size,
        String contentType
) {
}
