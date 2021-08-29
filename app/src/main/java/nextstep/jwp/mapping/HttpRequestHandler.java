package nextstep.jwp.mapping;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.presentation.Controller;

public class HttpRequestHandler implements Handler {

    private Controller controller;

    public HttpRequestHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws Exception {
        controller.service(request, response);
    }
}
