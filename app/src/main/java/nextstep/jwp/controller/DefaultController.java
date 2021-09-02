package nextstep.jwp.controller;

import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class DefaultController extends AbstractController {

    @Override
    public HttpResponse process(HttpRequest request) {
        return HttpResponse.of(HttpStatus.OK, new Body("Hello World!"));
    }
}
