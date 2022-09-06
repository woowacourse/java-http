package org.apache.coyote.http11.response;

public class HttpResponseBody {

    private final String bodyContext;

    public HttpResponseBody(final String bodyContext) {
        this.bodyContext = bodyContext;
    }

    public static HttpResponseBody empty() {
        return new HttpResponseBody("");
    }

    public String getBodyContext() {
        return bodyContext;
    }

    public int getContentLength() {
        return bodyContext.getBytes().length;
    }
}
