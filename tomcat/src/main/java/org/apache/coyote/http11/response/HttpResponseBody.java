package org.apache.coyote.http11.response;

public class HttpResponseBody {

    private final String body;

    public HttpResponseBody(final String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
