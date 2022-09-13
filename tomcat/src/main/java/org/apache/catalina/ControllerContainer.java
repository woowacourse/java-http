package org.apache.catalina;

import org.apache.coyote.Container;
import org.apache.coyote.Controller;
import org.apache.coyote.ExceptionHandler;
import org.apache.coyote.http11.request.Request;

public class ControllerContainer implements Container {
    private final RequestMapping requestMapping;
    private final ExceptionHandlers exceptionHandlers;

    public ControllerContainer(final RequestMapping requestMapping, final ExceptionHandlers exceptionHandlers) {
        this.requestMapping = requestMapping;
        this.exceptionHandlers = exceptionHandlers;
    }

    @Override
    public Controller findController(final Request request) {
        return requestMapping.map(request.getURL());
    }

    @Override
    public ExceptionHandler findExceptionHandler(final Exception exception) {
        return exceptionHandlers.find(exception);
    }
}
