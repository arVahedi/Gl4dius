package io.gl4dius.cli.model.dto.iptables;

public record IptablesRedirectRule(
        String inputInterface,
        int originalPort,
        String destinationIp,
        int destinationPort
) {

    public IptablesRedirectRule {
        if (inputInterface == null || inputInterface.isBlank()) {
            throw new IllegalArgumentException("inputInterface is required");
        }

        if (destinationIp == null || destinationIp.isBlank()) {
            throw new IllegalArgumentException("destinationIp is required");
        }

        validatePort(originalPort, "originalPort");

        validatePort(destinationPort, "destinationPort");
    }

    private static void validatePort(int port, String fieldName) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(fieldName + " must be between 1 and 65535");
        }
    }
}
