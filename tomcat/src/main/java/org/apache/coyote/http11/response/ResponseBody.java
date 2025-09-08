package org.apache.coyote.http11.response;


public record ResponseBody(byte[] bytes) {

    public ResponseBody(int size) {
        this(new byte[size]);
    }
}
