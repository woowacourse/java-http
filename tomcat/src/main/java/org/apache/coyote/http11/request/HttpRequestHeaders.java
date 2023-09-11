package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestHeaders {

    private final Map<String, String> headers;

    HttpRequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }

    String getCookie() {
        return headers.get("Cookie");
    }
}
