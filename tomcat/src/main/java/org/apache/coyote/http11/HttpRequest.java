package org.apache.coyote.http11;

public record HttpRequest(
        String httpMethod,
        String uri,
        double httpVersion,
        String host,
        String contentType,
        int contentLength,
        String requestBody,
        Cookie cookie
) {
    public HttpRequest(String httpMethod, String uri, double httpVersion, String host, String contentType,
                       String requestBody, Cookie cookie) {
        this(
                httpMethod,
                uri,
                httpVersion,
                host,
                contentType,
                requestBody == null ? 0 : requestBody.length(),
                requestBody,
                cookie
        );
    }
}
