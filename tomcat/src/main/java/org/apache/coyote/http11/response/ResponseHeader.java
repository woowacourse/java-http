package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.ContentType;

public class ResponseHeader {

    private final ContentType contentType;
    private final long contentLength;

    public ResponseHeader(ContentType contentType, long contentLength) {
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType.getContent();
    }

    public long getContentLength() {
        return contentLength;
    }
}
