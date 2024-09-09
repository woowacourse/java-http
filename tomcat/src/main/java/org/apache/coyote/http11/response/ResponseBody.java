package org.apache.coyote.http11.response;

public record ResponseBody(byte[] body) {

    public int size() {
        if (body == null || body.length == 0) {
            return 0;
        }
        return body.length;
    }
}
