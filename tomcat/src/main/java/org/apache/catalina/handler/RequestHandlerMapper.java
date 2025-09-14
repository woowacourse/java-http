package org.apache.catalina.handler;

import java.util.List;
import org.apache.catalina.RequestHandler;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public record RequestHandlerMapper(List<RequestHandler> requestHandlers) {

    public RequestHandler getRequestHandler(final Http11Request request, final Http11Response response) {
        return requestHandlers.stream().filter(requestHandler -> requestHandler.canHandle(request))
                .findFirst()
                .orElseThrow(() -> new PathNotFoundException(response));
    }
}
