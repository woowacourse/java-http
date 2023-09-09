package org.apache.coyote.handler.statichandler;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.IOException;

public class StaticHandler implements Handler {

    private static final String DEFAULT_DIRECTORY_PATH = "static";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            return HttpResponse.createStaticResponseByPath(
                    httpRequest.httpVersion(),
                    HttpStatus.OK,
                    DEFAULT_DIRECTORY_PATH + httpRequest.path()
            );
        } catch (NullPointerException | IOException e) {
            return new ExceptionHandler(HttpStatus.NOT_FOUND).handle(httpRequest);
        }
    }
}
