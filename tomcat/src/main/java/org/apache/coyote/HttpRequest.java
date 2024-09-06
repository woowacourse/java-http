package org.apache.coyote;

import java.util.Arrays;

public class HttpRequest {
    private final String method;
    private final String url;
    private final String path;
    private final String version;
    private final HttpHeader[] headers;
    private final String body;
    private final HttpCookie httpCookie;

    public HttpRequest(String method, String path, String version, HttpHeader[] headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.url = parseUrl(path);
        this.body = body;
        this.httpCookie = parseCookie(headers);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    private String parseUrl(final String path) {
        HttpHeader hostHeader = Arrays.stream(headers)
                .filter(header -> header.getKey().equalsIgnoreCase("host"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Host 헤더가 존재하지 않습니다."));

        return hostHeader.getValue() + path;
    }

    private HttpCookie parseCookie(HttpHeader[] headers) {
        return Arrays.stream(headers)
                .filter(header -> header.getKey().equalsIgnoreCase("Cookie"))
                .findFirst()
                .map(header -> new HttpCookie(header.getValue())).orElse(null);
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
