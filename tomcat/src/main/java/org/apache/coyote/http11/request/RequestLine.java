package org.apache.coyote.http11.request;

public record RequestLine(HttpMethod method, Path path, ProtocolVersion protocolVersion) {

    public static RequestLine from(String line) {
        validateLine(line);
        String[] parts = line.trim().split("\\s+");
        validateParts(parts);
        return new RequestLine(
            HttpMethod.from(parts[0]),
            Path.from(parts[1]),
            ProtocolVersion.from(parts[2])
        );
    }

    private static void validateLine(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("HTTP 요청 라인이 비어 있습니다.");
        }
    }

    private static void validateParts(String[] parts) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("HTTP 요청 라인의 형식이 올바르지 않습니다.");
        }
    }
}
