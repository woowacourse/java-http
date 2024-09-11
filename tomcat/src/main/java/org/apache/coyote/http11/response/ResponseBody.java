package org.apache.coyote.http11.response;

public class ResponseBody {

    private final byte[] body;

    public ResponseBody(byte[] body) {
        this.body = body;
    }

    public static ResponseBody empty() {
        return new ResponseBody(new byte[0]);
    }

    public int size() {
        if (body == null || body.length == 0) {
            return 0;
        }
        return body.length;
    }

    public byte[] getBody() {
        if (body == null || body.length == 0) {
            return new byte[0];
        }
        return body;
    }
}
