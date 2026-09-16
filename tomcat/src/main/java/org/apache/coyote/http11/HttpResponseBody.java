package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class HttpResponseBody {

    private final byte[] content;

    public HttpResponseBody(byte[] content) {
        this.content = content;
    }

    public byte[] getBytes() {
        return content;
    }

    public int length() {
        return content.length;
    }

    public String toString() {
        return new String(content, StandardCharsets.UTF_8);
    }
}
