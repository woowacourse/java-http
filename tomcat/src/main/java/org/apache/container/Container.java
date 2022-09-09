package org.apache.container;

import http.request.HttpRequest;
import http.response.HttpResponse;
import org.apache.container.config.Configuration;
import org.apache.container.exception.ContainerNotInitializedException;
import org.apache.container.handler.ExceptionHandler;
import org.apache.container.handler.RequestHandler;
import org.apache.container.handler.RequestMapper;

public class Container {

    private final RequestMapper requestMapper = new RequestMapper();

    public Container(final Configuration configuration) {
        configuration.setDefaultHandler(requestMapper);
        configuration.addHandlers(requestMapper);
        configuration.setExceptionHandler(requestMapper);
    }

    public HttpResponse respond(final HttpRequest httpRequest) {
        validateInitialized();
        try {
            RequestHandler requestHandler = requestMapper.findHandler(httpRequest.getUrl());
            return requestHandler.service(httpRequest);
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = requestMapper.getExceptionHandler();
            return exceptionHandler.handle(e);
        }
    }

    private void validateInitialized() {
        if (!requestMapper.isInitialized()) {
            throw new ContainerNotInitializedException();
        }
    }
}
