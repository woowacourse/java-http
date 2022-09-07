package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpRequest;

public class PostProcessMeta {

    private final HttpRequest request;
    private final ResponseBody body;

    public PostProcessMeta(HttpRequest request, ResponseBody body) {
        this.request = request;
        this.body = body;
    }

    public long bodyLength() {
        return body.getAsString().length();
    }

    public HttpRequest getRequest() {
        return request;
    }
}
