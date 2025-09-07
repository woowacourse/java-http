package org.apache.coyote.http11;

public class HttpRequestBody {
    private final byte[] body;

    public HttpRequestBody(byte[] body) {
        this.body = body;
    }

    public String asString() {
        return new String(body);
    }

    public byte[] getBody() {
        return body;
    }

    public int getLength() {
        return body.length;
    }
}
