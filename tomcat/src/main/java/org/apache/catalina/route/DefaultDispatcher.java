package org.apache.catalina.route;

import java.util.Optional;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultDispatcher implements Dispatcher {

    private final RequestMapping requestMapping;
    private final Controller notFoundController;

    public DefaultDispatcher(RequestMapping requestMapping, Controller notFoundController) {
        this.notFoundController = notFoundController;
        this.requestMapping = requestMapping;
    }

    public DefaultDispatcher(RequestMapping requestMapping) {
        this(requestMapping, null);
    }

    @Override
    public void dispatch(HttpRequest request, HttpResponse response) {
        requestMapping.getController(request).ifPresentOrElse(
                controller -> controller.service(request, response),
                () -> handleNotFound(request, response)
        );
    }

    private void handleNotFound(HttpRequest request, HttpResponse response) {
        Optional.ofNullable(notFoundController)
                .ifPresentOrElse(
                        controller -> controller.service(request, response),
                        () -> response.setStatus(HttpStatusCode.NOT_FOUND)
                );
    }
}
