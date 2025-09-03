package org.apache.coyote.http11;

public record HttpRequest(
        HttpMethod method,
        String resourcePath
) {

    public String extractContentExpansion() {
        final String[] split = resourcePath.split("\\.");
        if (split.length != 2) {
            throw new IllegalStateException("확장자가 없는 경우 지원하지 않는 메서드입니다.");
        }
        return split[1];
    }

    public boolean isStaticResource() {
        return resourcePath.contains(".");
    }

    public static HttpRequest from(String request) {
        final String requestLine = extractRequestLine(request);
        final String[] requestLineElements = requestLine.split(" ");
        if (requestLineElements.length != 3) {
            throw new IllegalArgumentException("request line이 올바르지 않습니다.");
        }
        HttpMethod method = HttpMethod.from(requestLineElements[0]);
        String resourcePath = requestLineElements[1];
        return new HttpRequest(method, resourcePath);
    }

    private static String extractRequestLine(String request) {
        final String[] split = request.split("\r\n");
        if (split.length == 0) {
            throw new IllegalArgumentException("요청이 비어있습니다.");
        }
        return split[0];
    }
}
