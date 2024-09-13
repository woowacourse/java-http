package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class HttpBody {

    private final byte[] content;

    public HttpBody(byte[] content) {
        if (content == null) {
            this.content = new byte[0];
            return;
        }
        this.content = content;
    }

    public HttpBody(String content) {
        this(content.getBytes(StandardCharsets.UTF_8));
    }

    public static HttpBody empty() {
        return new HttpBody("");
    }

    public long getContentLength() {
        return content.length;
    }

    public String getContent() {
        return new String(content, StandardCharsets.UTF_8);
    }
}
