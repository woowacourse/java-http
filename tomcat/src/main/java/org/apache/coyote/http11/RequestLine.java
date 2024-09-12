package org.apache.coyote.http11;

public record RequestLine(HttpMethod method, String uri, String version) {

    public static RequestLine from(String requestLine) {
        final String[] requestParts = requestLine.split(" ");
        final HttpMethod httpMethod = HttpMethod.from(requestParts[0]);
        final String uri = resolveDefaultPage(requestParts[1]);
        final String version = requestParts[2];
        return new RequestLine(httpMethod, uri, version);
    }

    private static String resolveDefaultPage(final String endPoint) {
        if (endPoint.equals("/") || endPoint.isEmpty()) {
            return "/index.html";
        }
        return endPoint;
    }
}
