package org.apache.coyote.http11.request;

import java.util.Map;

import static org.apache.coyote.http11.HttpHeaderKey.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeaderKey.COOKIE;

public class HttpRequestHeaders {

    private static final String contentTypeKey = "Accept";

    private static final String DELIMITER = ",";

    private final Map<String, String> headers;

    public HttpRequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getContentType() {
        return this.headers.get(contentTypeKey).split(DELIMITER)[0];
    }

    public int getContentLength() {
        return Integer.parseInt(this.headers.get(CONTENT_LENGTH.getKeyName()));
    }

    public String getCookies() {
        return this.headers.getOrDefault(COOKIE.getKeyName(), "");
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }
}
