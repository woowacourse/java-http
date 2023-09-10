package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.ContentType;

public class ResponseBody {
    public static final ResponseBody DEFAULT = new ResponseBody("", ContentType.HTML);
    private final String body;
    private final ContentType contentType;

    public ResponseBody(final String body, final ContentType contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    public String getContentLength() {
        return String.valueOf(body.getBytes().length);
    }

    public String getBody() {
        return body;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
