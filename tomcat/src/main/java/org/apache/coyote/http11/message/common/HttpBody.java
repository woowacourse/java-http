package org.apache.coyote.http11.message.common;

public class HttpBody {

    private String body;

    public HttpBody(String body) {
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
