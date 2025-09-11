package org.apache.coyote.http11.response;


public class ResponseBody {

    private byte[] bytes;

    public ResponseBody(final byte[] bytes) {
        this.bytes = bytes;
    }

    public ResponseBody(final int size) {
        this.bytes = new byte[size];
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(final byte[] bytes) {
        this.bytes = bytes;
    }
}
