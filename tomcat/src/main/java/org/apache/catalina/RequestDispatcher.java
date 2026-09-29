package org.apache.catalina;

import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceHandler;
import org.apache.coyote.http11.StatusLine;

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
            staticResourceHandler.handle(request.path(), response);
            return;
        }
        final DispatchResult result = controller.get()
            .service(request, response);

        dispatchResult(result, response);
    }

    private void dispatchResult(final DispatchResult result, final HttpResponse response)
        throws IOException {
        response.addStatusLine(StatusLine.http11(result.status()));
        switch (result.type()) {
            case FORWARD -> staticResourceHandler.handle(result.path(), response);
            case REDIRECT -> response.sendRedirect(result.path());
        }
    }

}
