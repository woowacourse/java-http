package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private final HttpStatus httpStatus;
    private final String content;
    private final Map<String, String> cookies;

    public Response(final HttpStatus httpStatus, final String content) {
        this.httpStatus = httpStatus;
        this.content = content;
        this.cookies = new HashMap<>();
    }

    public Response(final HttpStatus httpStatus, final String content, final Map<String, String> cookies) {
        this.httpStatus = httpStatus;
        this.content = content;
        this.cookies = cookies;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
