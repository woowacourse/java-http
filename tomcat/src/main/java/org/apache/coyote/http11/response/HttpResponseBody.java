package org.apache.coyote.http11.response;

public class HttpResponseBody {
    private String body;

    public HttpResponseBody() {
        body = "";
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
