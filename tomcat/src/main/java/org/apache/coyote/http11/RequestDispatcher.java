package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.controller.Controller;

public class RequestDispatcher {

    private final HandlerMapping handlerMapping;
    private final StaticResourceHandler staticResourceHandler =
        new StaticResourceHandler();

    public RequestDispatcher(final HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public void dispatch(final HttpRequest request, final HttpResponse response)
        throws Exception {
        final Optional<Controller> controller = handlerMapping.getController(request);
        if (controller.isEmpty()) {
            staticResourceHandler.handle(request, response);
            return;
        }
        controller.get()
            .service(request, response);

        handleForward(request, response);
    }

    private void handleForward(final HttpRequest request, final HttpResponse response)
        throws IOException {
        if (response.hasForwardPath()) {
            staticResourceHandler.handle(request, response);
        }
    }

}
