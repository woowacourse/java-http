package org.apache.coyote.request;

import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class RequestHeader {

    private static final String COOKIE = "Cookie";
    private static final String ACCEPT = "Accept";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    public RequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH, "0"));
    }

    public boolean hasRequestBody() {
        return headers.containsKey(CONTENT_LENGTH);
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(headers.getOrDefault(COOKIE, ""));
    }

    public String getResourceType() {
        return headers.get(ACCEPT);
    }
}
