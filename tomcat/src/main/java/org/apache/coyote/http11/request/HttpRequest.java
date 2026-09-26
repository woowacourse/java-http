package org.apache.coyote.http11.request;

public record HttpRequest(
        RequestLine requestLine,
        RequestParams params,
        HttpHeaders headers,
        byte[] body
) {

    public HttpRequest {
        body = body.clone();
    }

    public byte[] body() {
        return body.clone();
    }

    public String path() {
        return requestLine.path();
    }

    public boolean isPost() {
        return "POST".equals(requestLine.method());
    }
}
