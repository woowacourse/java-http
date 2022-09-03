package org.apache.coyote.http11.model.response;

import org.apache.coyote.http11.model.ContentType;

public class ResponseBody {

    private final ContentType contentType;
    private final String body;

    public ResponseBody(final String body, final ContentType contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    public int getContentLength() {
        return body.getBytes().length;
    }

    public String getContentType() {
        return contentType.getValue();
    }

    public String getBody() {
        return body;
    }
}
