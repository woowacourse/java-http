package org.apache.coyote.http11.adaptor;

import static java.util.Collections.synchronizedSet;

import java.util.Set;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.handler.Controller;

public class ControllerAdaptor {

    private final Set<Controller> handlers;

    public ControllerAdaptor(final Set<Controller> handlers) {
        this.handlers = synchronizedSet(handlers);
    }

    public void handle(HttpRequest request, HttpResponse response) {
        Controller controller = handlers.stream()
                .filter(handler -> handler.support(request))
                .findFirst()
                .orElseThrow();
        controller.handle(request, response);
    }
}
