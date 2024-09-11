package org.apache.http.request;

import java.util.Arrays;
import java.util.Optional;

import org.apache.http.HttpCookie;
import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;
import org.apache.http.header.HttpHeader;
import org.apache.http.header.StandardHttpHeader;

public class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeader[] headers;
    private final String body;
    private final HttpCookie httpCookie;

    public HttpRequest(RequestLine requestLine, HttpHeader[] headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.httpCookie = parseCookie(headers);
    }

    public HttpRequest(String method, String path, String version, HttpHeader[] headers, String body) {
        this.requestLine = new RequestLine(method, path, version);
        this.headers = headers;
        this.body = body;
        this.httpCookie = parseCookie(headers);
    }

    private HttpCookie parseCookie(HttpHeader[] headers) {
        return Optional.ofNullable(headers)
                .flatMap(hs -> Arrays.stream(hs)
                        .filter(header -> StandardHttpHeader.COOKIE.equalsIgnoreCase(header.getKey()))
                        .findFirst()
                        .map(header -> HttpCookie.of(header.getValue())))
                .orElse(null);
    }

    public String getFormBody(String key) {
        final String[] params = this.body.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] keyAndValue = params[i].split("=");
            if (keyAndValue[0].equals(key)) {
                return keyAndValue[1];
            }
        }
        return null;
    }

    public boolean isSameMethod(HttpMethod httpMethod) {
        return requestLine.hasSameMethod(httpMethod);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpVersion getVersion() {
        return requestLine.getVersion();
    }

    public HttpHeader[] getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        for (HttpHeader header : headers) {
            if (header.getKey().equals(key)) {
                return header.getValue();
            }
        }
        throw new IllegalArgumentException("존재 하지 않는 Header " + key + "입니다.");
    }

    public String getBody() {
        return body;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
