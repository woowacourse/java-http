package nextstep.jwp.controller;

import nextstep.jwp.web.HttpMethod;
import nextstep.jwp.web.HttpRequest;
import nextstep.jwp.web.HttpResponse;
import nextstep.jwp.web.HttpStatus;

public class AbstractController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod requestMethod = request.getMethod();

        if (requestMethod.equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (requestMethod.equals(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        responseMethodNotAllowed(response);
    }

    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        responseMethodNotAllowed(response);
    }

    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        responseMethodNotAllowed(response);

    }

    private void responseMethodNotAllowed(HttpResponse response) {
        response.status(HttpStatus.METHOD_NOT_ALLOWED);
    }

}
