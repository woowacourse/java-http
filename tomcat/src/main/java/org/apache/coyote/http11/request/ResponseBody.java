package org.apache.coyote.http11.request;

public class ResponseBody {

    String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
