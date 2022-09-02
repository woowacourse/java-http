package org.apache.coyote.http11.response;

public class HttpResponseBody {

    private final String bodyContext;
    private final int contentLength;

    public HttpResponseBody(String bodyContext) {
        this.bodyContext = bodyContext;
        this.contentLength = bodyContext.getBytes().length;
    }

    public String getResponseBody() {
        return bodyContext;
    }

    public int getContentLength() {
        return contentLength;
    }
}
