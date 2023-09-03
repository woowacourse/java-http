package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.util.ResourceResolver;

public class NotFoundHandler implements Handler {

    public static final NotFoundHandler INSTANCE = new NotFoundHandler();

    private NotFoundHandler() {
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setContentType(HttpContentType.TEXT_HTML);
        response.setContentBody(ResourceResolver.resolve("/404.html"));
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        return response;
    }
}
