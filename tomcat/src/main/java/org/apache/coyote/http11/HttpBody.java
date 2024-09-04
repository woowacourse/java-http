package org.apache.coyote.http11;

public class HttpBody {

    private final String body;

    public HttpBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
