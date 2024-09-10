package org.apache.coyote.http11;

public class Http11ResponseContent {

    private final String contentType;
    private final int contentLength;
    private final String body;

    public Http11ResponseContent(String path, String body) {
        this.contentType = Http11ContentTypeParser.parse(path);
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
