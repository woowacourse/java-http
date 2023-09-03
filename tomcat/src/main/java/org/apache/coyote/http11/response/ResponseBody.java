package org.apache.coyote.http11.response;

public class ResponseBody {
    private final ContentType contentType;
    private final String content;

    public ResponseBody(final ContentType contentType, final String content) {
        this.contentType = contentType;
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
