package org.apache.coyote.http11;

import java.nio.charset.Charset;

public record HttpBody(byte[] bytes) {

    public HttpBody(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    public String asString(Charset charset) {
        return new String(bytes, charset);
    }

    public int length() {
        return bytes.length;
    }

    public boolean isEmpty() {
        return bytes.length == 0;
    }

    @Override
    public byte[] bytes() {
        return bytes.clone();
    }
}
