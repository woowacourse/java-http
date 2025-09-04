package org.apache.coyote.http11.constant;

public record RequestLine(HttpMethod method, ResourcePath resourcePath, String version) {

    public static RequestLine from(String requestLine) {
        final String stripRequestLine = requestLine.strip();
        final String[] parts = stripRequestLine.split("\\s+");
        validateRequestLineFormat(parts);
        final HttpMethod method = HttpMethod.from(parts[0]);
        final ResourcePath resourcePath = new ResourcePath(parts[1]);
        final String version = parts[2];
        return new RequestLine(method, resourcePath, version);
    }

    public static String extractRequestLine(String request) {
        validateRequestFormat(request);
        final String[] split = request.split("\r\n");
        if (split.length == 0) {
            throw new IllegalArgumentException("요청이 비어있습니다.");
        }
        return split[0];
    }

    private static void validateRequestFormat(String request) {
        if (request == null || request.isBlank()) {
            throw new IllegalArgumentException("요청이 비어있습니다.");
        }
    }

    private static void validateRequestLineFormat(String[] requestLine) {
        if (requestLine.length != 3) {
            throw new IllegalArgumentException("욜바르지 않은 request line 입니다.");
        }
    }
}
