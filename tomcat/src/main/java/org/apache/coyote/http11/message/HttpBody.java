package org.apache.coyote.http11.message;

import java.nio.charset.StandardCharsets;

public class HttpBody {

    private final byte[] content;

    private HttpBody(byte[] content) {
        this.content = content;
    }

    public static HttpBody fromString(String body) {
        return new HttpBody(body.getBytes(StandardCharsets.UTF_8));
    }

    public static HttpBody fromBytes(byte[] body) {
        return new HttpBody(body);
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
}
