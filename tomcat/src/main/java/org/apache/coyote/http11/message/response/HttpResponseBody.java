package org.apache.coyote.http11.message.response;

public class HttpResponseBody {

    private String body;

    public HttpResponseBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return body;
    }
}
