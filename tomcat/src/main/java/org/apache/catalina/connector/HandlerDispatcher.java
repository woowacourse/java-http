package org.apache.catalina.connector;

import java.util.List;
import org.apache.catalina.exception.ExceptionHandler;
import org.apache.catalina.exception.Http4xxException;
import org.apache.catalina.exception.PathNotFoundException;
import org.apache.catalina.handler.RequestHandler;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class HandlerDispatcher {

    private final List<RequestHandler> requestHandlers;
    private final ExceptionHandler exceptionHandler;

    public HandlerDispatcher(final List<RequestHandler> requestHandlers, final ExceptionHandler exceptionHandler) {
        this.requestHandlers = requestHandlers;
        this.exceptionHandler = exceptionHandler;
    }

    public void handle(final Http11Request request, final Http11Response response) {
        try {
            final RequestHandler requestHandler = getRequestHandler(request, response);
            requestHandler.handle(request, response);
        } catch (Http4xxException e) {
            exceptionHandler.handle(e, request, response);
        } catch (Exception e) {
            response.setState(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionHandler.handle(e, request, response);
        }
    }

    private RequestHandler getRequestHandler(final Http11Request request, final Http11Response response) {
        return requestHandlers.stream().filter(requestHandler -> requestHandler.canHandle(request))
                .findFirst()
                .orElseThrow(() -> new PathNotFoundException(response));
    }
}
