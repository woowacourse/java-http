package org.apache.coyote.http.request;

import org.apache.coyote.http.util.HttpConsts;

public class HttpRequestBody {

    public static final HttpRequestBody EMPTY = new HttpRequestBody(HttpConsts.BLANK);

    private final String body;

    public HttpRequestBody(final String body) {
        this.body = body;
    }

    public boolean isEmpty() {
        return body == null || body.isEmpty() || body.isBlank();
    }

    public String body() {
        return body;
    }
}
