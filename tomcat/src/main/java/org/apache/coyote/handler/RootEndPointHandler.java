package org.apache.coyote.handler;

import org.apache.http.header.HttpHeaderName;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class RootEndPointHandler extends Handler {
    private static final RootEndPointHandler INSTANCE = new RootEndPointHandler();

    private RootEndPointHandler() {
    }

    public static RootEndPointHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return HttpResponse.builder()
                .body("Hello world!")
                .addHeader(HttpHeaderName.CONTENT_TYPE, "text/plain")
                .build();
    }
}
