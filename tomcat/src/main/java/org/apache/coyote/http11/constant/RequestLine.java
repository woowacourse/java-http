package org.apache.coyote.http11.constant;

public record RequestLine(HttpMethod method, ResourcePath resourcePath, String version) {

    public static RequestLine from(String requestLine) {
        if (requestLine == null || requestLine.trim().isEmpty()) {
            return null;
        }
        final String stripRequestLine = requestLine.strip();
        final String[] parts = stripRequestLine.split("\\s+");
        validateRequestLineFormat(parts);
        final HttpMethod method = HttpMethod.from(parts[0]);
        final ResourcePath resourcePath = new ResourcePath(parts[1]);
        final String version = parts[2];
        return new RequestLine(method, resourcePath, version);
    }

    private static void validateRequestLineFormat(String[] requestLine) {
        if (requestLine.length != 3) {
            throw new IllegalArgumentException("욜바르지 않은 request line 입니다.");
        }
    }
}
