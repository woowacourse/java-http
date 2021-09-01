package nextstep.jwp.web.mvc.controller;

import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;

public class NotFoundController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.headers().setLocation("/404.html");
    }
}
