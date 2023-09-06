package org.apache.coyote.http11.handler;

import java.util.List;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;

public class HttpHandlers {

    private static final List<HttpHandler> HTTP_HANDLERS = List.of(
            new ResourceHandler(),
            new LoginHandler(),
            new RegisterHandler()
    );

    public ResponseEntity handle(final HttpRequest request) {
        try {
            return HTTP_HANDLERS.stream()
                    .filter(httpHandler -> httpHandler.canHandle(request))
                    .findFirst()
                    .orElseGet(NotFoundHandler::new)
                    .handle(request);
        } catch (RuntimeException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "/500");
        }
    }
}
