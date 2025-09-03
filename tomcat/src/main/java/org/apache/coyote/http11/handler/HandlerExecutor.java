package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HandlerExecutor {

    private final HttpRequestHandler notFoundHandler = new NotFoundHandler();
    private final List<HttpRequestHandler> handlers = List.of(
            new HelloWorldHandler(),
            new StaticFileHandler(notFoundHandler)
    );

    public HttpResponse execute(HttpRequest request) throws IOException {
        for (HttpRequestHandler handler : handlers) {
            if (handler.canHandle(request)) {
                return handler.handle(request);
            }
        }
        return notFoundHandler.handle(request);
    }
}
