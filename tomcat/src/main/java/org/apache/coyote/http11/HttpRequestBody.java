package org.apache.coyote.http11;

public class HttpRequestBody {

    private final String body;

    public HttpRequestBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
