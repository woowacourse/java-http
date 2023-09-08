package org.apache.coyote.http11.request;

import java.util.Objects;

public class RequestLine {

    private static final String TOKEN_DELIMITER = " ";

    private final String httpMethod;
    private final String requestUri;
    private final String httpVersion;

    private RequestLine(String httpMethod, String requestUri, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] splitStatusLine = Objects.requireNonNull(requestLine.trim()).split(TOKEN_DELIMITER);
        String httpMethod = splitStatusLine[0];
        String requeatUri = splitStatusLine[1];
        String httpVersion = splitStatusLine[2];
        return new RequestLine(httpMethod, requeatUri, httpVersion);
    }

    public boolean equalsMethod(String other) {
        return this.httpMethod.equalsIgnoreCase(other);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
