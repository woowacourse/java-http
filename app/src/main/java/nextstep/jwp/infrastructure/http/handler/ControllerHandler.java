package nextstep.jwp.infrastructure.http.handler;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public class ControllerHandler implements Handler {

    private final Controller controller;

    public ControllerHandler(final Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) throws Exception {
        controller.service(request, response);
    }
}
