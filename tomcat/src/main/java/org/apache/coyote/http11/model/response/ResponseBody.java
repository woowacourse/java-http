package org.apache.coyote.http11.model.response;

import org.apache.coyote.http11.model.ContentFormat;

public class ResponseBody {

    private final ContentFormat contentFormat;
    private final String body;

    public ResponseBody(final String body, final ContentFormat contentFormat) {
        this.body = body;
        this.contentFormat = contentFormat;
    }

    public int getContentLength() {
        return body.getBytes().length;
    }

    public String getContentType() {
        return contentFormat.getValue();
    }

    public String getBody() {
        return body;
    }
}
