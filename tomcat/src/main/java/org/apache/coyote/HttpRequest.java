package org.apache.coyote;

import java.util.Arrays;

public class HttpRequest {
    private final String method;
    private final String url;
    private final String path;
    private final String version;
    private final HttpHeader[] headers;
    private final String body;

    public HttpRequest(String method, String path, String version, HttpHeader[] headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.url = parseUrl(path);
        this.body = body;
    }

    public HttpRequest(String method, String path, String version, HttpHeader[] headers) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.url = parseUrl(path);
        this.body = null;
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
}
