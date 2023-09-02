package org.apache.coyote.http11.httpmessage;

public class ResponseBody {

    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return body;
    }
}
