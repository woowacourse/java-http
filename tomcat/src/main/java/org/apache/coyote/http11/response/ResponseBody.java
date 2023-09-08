package org.apache.coyote.http11.response;

import org.apache.coyote.http11.types.ContentType;

public class ResponseBody {

    private final String body;
    private final ContentType contentType;

    public ResponseBody(String body, ContentType contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
