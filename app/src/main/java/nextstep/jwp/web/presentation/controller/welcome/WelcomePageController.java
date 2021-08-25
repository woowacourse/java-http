package nextstep.jwp.web.presentation.controller.welcome;

import nextstep.jwp.http.header.element.Body;
import nextstep.jwp.http.header.element.HttpStatus;
import nextstep.jwp.http.header.request.HttpRequest;
import nextstep.jwp.http.header.request.request_line.HttpMethod;
import nextstep.jwp.http.header.response.HttpResponse;
import nextstep.jwp.http.header.response.Response;
import nextstep.jwp.web.presentation.controller.CustomController;

public class WelcomePageController extends CustomController {

    @Override
    public Response doService(HttpRequest httpRequest) {
        return HttpResponse.status(HttpStatus.OK, new Body("Hello world!"));
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.GET;
    }

    @Override
    protected String path() {
        return "/";
    }
}
