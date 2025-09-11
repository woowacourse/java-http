package org.apache.catalina.core;

import org.apache.catalina.RequestHandler;
import org.apache.catalina.exception.ExceptionHandler;
import org.apache.catalina.exception.Http4xxException;
import org.apache.catalina.handler.RequestHandlerMapper;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class CatalinaContainer {

    private final RequestHandlerMapper requestHandlerMapper;
    private final ExceptionHandler exceptionHandler;

    public CatalinaContainer(final RequestHandlerMapper requestHandlerMapper, final ExceptionHandler exceptionHandler) {
        this.requestHandlerMapper = requestHandlerMapper;
        this.exceptionHandler = exceptionHandler;
    }

    public void handle(final Http11Request request, final Http11Response response) {
        try {
            final RequestHandler requestHandler = requestHandlerMapper.getRequestHandler(request, response);
            requestHandler.handle(request, response);
        } catch (Http4xxException e) {
            exceptionHandler.handle(e, request, response);
        } catch (Exception e) {
            response.setState(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionHandler.handle(e, request, response);
        }
    }
}
