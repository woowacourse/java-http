package org.apache.coyote.http11.constant;

public record RequestLine(HttpMethod method, ResourcePath resourcePath, HttpVersion version) {

    public static RequestLine from(String requestLine) {
        validateRequestLineFormat(requestLine);
        final String[] requestLineElements = requestLine.split(" ");
        final HttpMethod method = HttpMethod.from(requestLineElements[0]);
        final ResourcePath resourcePath = new ResourcePath(requestLineElements[1]);
        final HttpVersion version = HttpVersion.from(requestLineElements[2]);
        return new RequestLine(method, resourcePath, version);
    }

    public static String extractRequestLine(String request) {
        final String[] split = request.split("\n");
        if (split.length == 0) {
            throw new IllegalArgumentException("요청이 비어있습니다.");
        }
        return split[0];
    }

    private static void validateRequestLineFormat(String requestLine) {
        long delimiterCount = requestLine.chars().filter(ch -> ch == ' ').count();
        if (delimiterCount != 2) {
            throw new IllegalArgumentException("욜바르지 않은 request line 입니다.");
        }
    }
}
