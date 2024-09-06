package org.apache.coyote.http11.httpresponse;

public class HttpResponseBody {

    private final String body;

    public HttpResponseBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
