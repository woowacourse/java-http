package org.apache.coyote.http11.handler;

import java.util.List;
import org.apache.coyote.http11.exception.InternalServerException;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HandlerExecutor {

    private final List<HttpRequestHandler> handlers = List.of(
            new HelloWorldHandler(),
            new StaticFileHandler(),
            new LoginHandler()
    );

    public HttpResponse execute(HttpRequest request) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(request))
                .findFirst()
                .map(handler -> {
                    try {
                        return handler.handle(request);
                    } catch (NotFoundException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new InternalServerException(e);
                    }
                })
                .orElseThrow(NotFoundException::new);
    }
}
