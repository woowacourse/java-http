package org.apache.coyote;

import org.apache.coyote.http11.Http11ContentTypeParser;

public class HttpResponseContent {

    private final String contentType;
    private final int contentLength;
    private final String body;

    public HttpResponseContent(String path, String body) {
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
