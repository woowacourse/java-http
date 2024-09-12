package org.apache.coyote.handler.exception;

import org.apache.coyote.handler.Handler;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class UnAuthorizationHandler extends Handler {
    private static final UnAuthorizationHandler INSTANCE = new UnAuthorizationHandler();

    private UnAuthorizationHandler() {
    }

    public static UnAuthorizationHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        return HttpResponse.builder()
                .unauthorizedBuild();
    }
}
