package org.apache.catalina.core.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();
    private Controller exceptionHandler;
    private Controller resourceController;

    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            Controller controller = findController(request.getPath());
            controller.service(request, response);
        } catch (final Exception e) {
            exceptionHandler.service(request, response);
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

    public void setExceptionHandler(final Controller exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void setResourceController(final Controller resourceController) {
        this.resourceController = resourceController;
    }
}
