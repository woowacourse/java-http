package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class ResponseBody {

    private final String content;

    public ResponseBody(String content) {
        this.content = content;
    }

    public int getContentLength() {
        return content.getBytes(StandardCharsets.UTF_8).length;
    }

    public String getContent() {
        return content;
    }
}
