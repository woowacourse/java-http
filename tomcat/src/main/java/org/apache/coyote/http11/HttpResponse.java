package org.apache.coyote.http11;

public class HttpResponse {

    private final String body;
    private final String contentType;

    public HttpResponse(final String body, final String contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return contentType;
    }
}
