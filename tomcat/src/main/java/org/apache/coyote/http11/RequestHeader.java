package org.apache.coyote.http11;

public record RequestHeader(
        RequestLine requestLine,
        Headers headers
) {

    public static RequestHeader from(String firstLine, Headers headers) {
        String[] splitRequestLine = firstLine.split(" ");
        String method = splitRequestLine[0];
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        String uri = splitRequestLine[1];
        String protocolVersion = splitRequestLine[2];
        RequestLine requestLine = new RequestLine(httpMethod, uri, protocolVersion);
        return new RequestHeader(requestLine, headers);
    }

    public int getContentLength() {
        return headers.getContentLength();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryString() {
        return requestLine.getQueryString();
    }

    public ContentType getContentType() {
        return headers.getContentType();
    }

    public String getJSessionId() {
        return headers.getJSessionId();
    }

    public boolean hasJSessionId() {
        return headers.hasJSessionId();
    }

    public HttpMethod getMethod() {
        return requestLine.method();
    }

    public HttpCookie getHttpCookie() {
        return headers.getHttpCookie();
    }
}
