package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpHeaders;
import org.apache.coyote.common.HttpProtocol;
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
        String body = ResourceResolver.resolve("/404.html");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType("text/html;charset=utf-8");
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.NOT_FOUND, headers);
        response.setContentBody(body);
        return response;
    }
}
