package org.apache.coyote.http11.message;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HttpBody {

    private final byte[] content;

    private HttpBody(byte[] content) {
        this.content = content;
    }

    public static HttpBody init() {
        return new HttpBody(new byte[0]);
    }

    public static HttpBody from(byte[] body) {
        return new HttpBody(Arrays.copyOf(body, body.length));
    }

    public static HttpBody from(String body) {
        return new HttpBody(body.getBytes(StandardCharsets.UTF_8));
    }

    public HttpBody append(byte[] additionalContent) {
        byte[] newContent = new byte[this.content.length + additionalContent.length];
        System.arraycopy(this.content, 0, newContent, 0, this.content.length);
        System.arraycopy(additionalContent, 0, newContent, this.content.length, additionalContent.length);
        return new HttpBody(newContent);
    }

    public HttpBody append(String additionalText) {
        return append(additionalText.getBytes(StandardCharsets.UTF_8));
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
