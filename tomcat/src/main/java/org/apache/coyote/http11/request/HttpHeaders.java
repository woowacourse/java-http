package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Objects;

public class HttpHeaders {
    public static final String COOKIE = "Cookie";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String,String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getCookie() {
        return headers.get(COOKIE);
    }

    public boolean haveContentLength() {
        return Objects.nonNull(headers.get(CONTENT_LENGTH));
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }
}
