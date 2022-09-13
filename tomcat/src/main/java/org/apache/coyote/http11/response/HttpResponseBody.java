package org.apache.coyote.http11.response;

public class HttpResponseBody {

    private static final String EMPTY = "";

    private final String value;

    private HttpResponseBody(final String value) {
        this.value = value;
    }

    public static HttpResponseBody init() {
        return new HttpResponseBody(EMPTY);
    }

    public static HttpResponseBody from(final String value) {
        return new HttpResponseBody(value);
    }

    public String getValue() {
        return value;
    }
}
