package org.apache.catalina.route;

import java.util.Optional;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultDispatcher implements Dispatcher {

    private final RequestMapping requestMapping;
    private Controller notFoundHandler;
    private Controller unresolvedErrorHandler;

    public DefaultDispatcher(
            RequestMapping requestMapping, Controller notFoundHandler, Controller unresolvedErrorHandler
    ) {
        this.requestMapping = requestMapping;
        this.notFoundHandler = notFoundHandler;
        this.unresolvedErrorHandler = unresolvedErrorHandler;
    }

    public DefaultDispatcher(RequestMapping requestMapping) {
        this(requestMapping, null, null);
    }

    @Override
    public void dispatch(HttpRequest request, HttpResponse response) {
        try {
            requestMapping.getController(request).ifPresentOrElse(
                    controller -> controller.service(request, response),
                    () -> handleError(request, response, notFoundHandler, HttpStatusCode.NOT_FOUND)
            );
        } catch (Exception e) {
            handleError(request, response, unresolvedErrorHandler, HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void handleError(
            HttpRequest request, HttpResponse response,
            Controller handler, HttpStatusCode defaultStatus
    ) {
        Optional.ofNullable(handler)
                .ifPresentOrElse(
                        controller -> controller.service(request, response),
                        () -> response.setStatus(defaultStatus)
                );
    }

    public void setNotFoundHandler(Class<? extends Controller> notFoundHandler) {
        this.notFoundHandler = instantiate(notFoundHandler);
    }

    public void setUnresolvedErrorHandler(Class<? extends Controller> unresolvedErrorHandler) {
        this.unresolvedErrorHandler = instantiate(unresolvedErrorHandler);
    }

    private Controller instantiate(Class<? extends Controller> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Controller must have a default constructor");
        }
    }
}
