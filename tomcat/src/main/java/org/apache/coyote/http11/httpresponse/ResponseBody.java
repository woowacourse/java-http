package org.apache.coyote.http11.httpresponse;

import java.nio.charset.StandardCharsets;

public class ResponseBody {

    private final String contentType;
    private final byte[] content;

    public ResponseBody(final String contentType, final byte[] content) {
        this.contentType = contentType;
        this.content = content;
    }

    public static ResponseBody createEmptyResponseBody() {
        return new ResponseBody("text/html;charset=utf-8", new byte[0]);
    }

    public String getContentType() {
        return contentType;
    }

    public int getLength() {
        return content.length;
    }

    public String toResponseText() {
        return new String(content, StandardCharsets.UTF_8);
    }
}
