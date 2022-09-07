package org.apache.coyote.http11.response;

public class HttpResponseBody {

    private final String value;

    public HttpResponseBody(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
