package org.apache.coyote.handler.exception;

import org.apache.coyote.handler.Handler;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class NotFoundHandler extends Handler {
    private static final NotFoundHandler INSTANCE = new NotFoundHandler();

    private NotFoundHandler() {
    }

    public static NotFoundHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        return HttpResponse.builder()
                .notFoundBuild();
    }
}
