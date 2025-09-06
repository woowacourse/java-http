package org.apache.coyote.http11.message;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HttpBody {

    private final byte[] content;

    private HttpBody(byte[] content) {
        this.content = content;
    }

    public static HttpBody from(String body) {
        return new HttpBody(body.getBytes(StandardCharsets.UTF_8));
    }

    public static HttpBody from(byte[] body) {
        return new HttpBody(Arrays.copyOf(body, body.length));
    }

    public static HttpBody empty() {
        return new HttpBody(new byte[0]);
    }

    public String toText() {
        return new String(content, StandardCharsets.UTF_8);
    }

    public int length() {
        return content.length;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(content, content.length);
    }
}
