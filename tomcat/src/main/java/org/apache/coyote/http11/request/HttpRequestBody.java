package org.apache.coyote.http11.request;

import org.apache.commons.lang3.StringUtils;

public class HttpRequestBody {
    private final String body;

    private HttpRequestBody(final String body) {
        this.body = body;
    }

    public static HttpRequestBody from(final String httpRequestBody) {
        return new HttpRequestBody(httpRequestBody);
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(StringUtils.EMPTY);
    }

    public String getBody() {
        return body;
    }
}
