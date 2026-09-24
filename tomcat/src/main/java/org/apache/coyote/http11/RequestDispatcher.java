package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.controller.Controller;

public class RequestDispatcher {

    private final HandlerMapping handlerMapping = new HandlerMapping();
    private final StaticResourceHandler staticResourceHandler =
        new StaticResourceHandler();

    public void dispatch(final HttpRequest request, final HttpResponse response)
        throws Exception {
        final Optional<Controller> controller = handlerMapping.getController(request);
        if (controller.isEmpty()) {
            staticResourceHandler.handle(request, response);
            return;
        }
        controller.get()
            .service(request, response);

        setJSessionId(request, response);
        handleForward(request, response);
    }


    private void setJSessionId(final HttpRequest request, HttpResponse response) {
        request.createdSession()
            .ifPresent(session ->
                response.addHeader("Set-Cookie", "JSESSIONID=" + session.id()));
    }

    private void handleForward(final HttpRequest request, final HttpResponse response)
        throws IOException {
        if (response.hasForwardPath()) {
            staticResourceHandler.handle(request, response);
        }
    }

}
