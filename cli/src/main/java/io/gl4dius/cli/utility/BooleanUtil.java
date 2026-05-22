package io.gl4dius.cli.utility;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class BooleanUtil {

    public boolean parseBoolean(@NonNull String rawValue) {
        return switch (rawValue.toLowerCase()) {
            case "true", "yes", "y", "1", "on" -> true;
            case "false", "no", "n", "0", "off" -> false;
            default -> throw new IllegalArgumentException("Invalid boolean value: " + rawValue);
        };
    }
}
