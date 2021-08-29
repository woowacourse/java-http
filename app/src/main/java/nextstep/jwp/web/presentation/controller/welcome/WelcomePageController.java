package nextstep.jwp.web.presentation.controller.welcome;

import nextstep.jwp.http.message.element.Body;
import nextstep.jwp.http.message.element.HttpStatus;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.http.message.response.HttpResponse;
import nextstep.jwp.http.message.response.Response;
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
