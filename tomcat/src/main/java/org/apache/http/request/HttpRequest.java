package org.apache.http.request;

import java.util.Arrays;
import java.util.Optional;

import org.apache.http.HttpCookie;
import org.apache.http.header.HttpHeader;
import org.apache.http.HttpMethod;

public class HttpRequest {
    private final HttpMethod method;
    private final String path;
    private final String version;
    private final HttpHeader[] headers;
    private final String body;
    private final HttpCookie httpCookie;

    public HttpRequest(String method, String path, String version, HttpHeader[] headers, String body) {
        this.method = HttpMethod.valueOf(method);
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.httpCookie = parseCookie(headers);
    }

    private HttpCookie parseCookie(HttpHeader[] headers) {
        return Optional.ofNullable(headers)
                .flatMap(hs -> Arrays.stream(hs)
                        .filter(header -> header.getKey().equalsIgnoreCase("Cookie"))
                        .findFirst()
                        .map(header -> HttpCookie.of(header.getValue())))
                .orElse(null);
    }

    public boolean isSameMethod(HttpMethod httpMethod) {
        return this.method == httpMethod;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
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
