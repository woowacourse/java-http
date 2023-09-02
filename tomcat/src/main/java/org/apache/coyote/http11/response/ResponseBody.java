package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;

public class ResponseBody {

    public static final ResponseBody EMPTY = new ResponseBody("", ContentType.HTML);

    private final String body;
    private final ContentType contentType;

    public ResponseBody(String body, ContentType contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    public static ResponseBody of(String body, String fileExtension) {
        return new ResponseBody(body, ContentType.parseContentType(fileExtension));
    }

    public String getBody() {
        return body;
    }

    public int getLength() {
        return body.getBytes().length;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
