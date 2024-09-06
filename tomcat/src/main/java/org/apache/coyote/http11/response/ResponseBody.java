package org.apache.coyote.http11.response;

public class ResponseBody {

    private final byte[] body;

    public ResponseBody(byte[] body) {
        this.body = body;
    }

    public int getBodyLength() {
        if (isEmpty()) {
            return 0;
        }
        return body.length;
    }

    public boolean isEmpty() {
        return body == null || body.length == 0;
    }

    public byte[] getBody() {
        return body;
    }
}
