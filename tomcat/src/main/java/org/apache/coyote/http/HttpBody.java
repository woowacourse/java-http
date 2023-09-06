package org.apache.coyote.http;

public class HttpBody {

    private static final HttpBody EMPTY = new HttpBody("");

    private final String value;

    public HttpBody(final String value) {
        this.value = value;
    }

    public static HttpBody empty() {
        return EMPTY;
    }
}
