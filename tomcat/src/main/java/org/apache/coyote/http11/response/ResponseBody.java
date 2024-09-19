package org.apache.coyote.http11.response;

public class ResponseBody {

    private static final int ZERO = 0;

    private final byte[] values;

    public ResponseBody(byte[] values) {
        this.values = values;
    }

    public int getLength() {
        if (isEmpty()) {
            return ZERO;
        }
        return values.length;
    }

    public boolean isEmpty() {
        return values == null || values.length == ZERO;
    }

    public byte[] getContents() {
        return values;
    }
}
