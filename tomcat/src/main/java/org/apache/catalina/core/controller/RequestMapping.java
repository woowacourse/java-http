package org.apache.catalina.core.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.java.servlet.Controller;
import nextstep.java.servlet.ExceptionHandler;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private Controller resourceController;
    private ExceptionHandler exceptionHandler;

    public void service(final HttpRequest request, final HttpResponse response) {
        try {
            Controller controller = findController(request.getPath());
            controller.service(request, response);
        } catch (final Exception exception) {
            exceptionHandler.handle(exception, response);
        }
    }

    private Controller findController(final String path) {
        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }
        return resourceController;
    }

    public void addController(final String path, final Controller controller) {
        this.controllers.put(path, controller);
    }

    public void setResourceController(final Controller resourceController) {
        this.resourceController = resourceController;
    }

    public void setExceptionHandler(final ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
