package org.apache.coyote;

public class ResponseContent {
    private final String contentType;
    private final int contentLength;
    private final String body;


    public ResponseContent(String contentType, String body) {
        this.contentType = contentType;
        this.contentLength = body.getBytes().length;
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getBody() {
        return body;
    }
}
