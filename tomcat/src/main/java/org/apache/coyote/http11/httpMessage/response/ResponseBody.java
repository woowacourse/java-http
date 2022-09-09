package org.apache.coyote.http11.httpmessage.response;

public class ResponseBody {

    private String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public void addBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return body;
    }
}
