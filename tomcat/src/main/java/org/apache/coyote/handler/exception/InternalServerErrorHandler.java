package org.apache.coyote.handler.exception;

import org.apache.coyote.handler.Handler;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class InternalServerErrorHandler extends Handler {
    private static final InternalServerErrorHandler INSTANCE = new InternalServerErrorHandler();

    private InternalServerErrorHandler() {
    }

    public static InternalServerErrorHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        return HttpResponse.builder()
                .internalServerErrorBuild();
    }
}

