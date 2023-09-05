package org.apache.coyote.response;

public class HttpResponseBody {

    private String body;

    public HttpResponseBody() {
    }

    public void setBody(final String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return body;
    }
}
