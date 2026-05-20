package io.gl4dius.cli.utility;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class UuidUtil {

    public Optional<UUID> parseUuidIfValid(String identifier) {
        try {
            return Optional.of(UUID.fromString(identifier));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
