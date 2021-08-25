package nextstep.jwp.http.controller.custom;

import nextstep.jwp.http.Body;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Response;

public class WelcomePageController extends CustomController{

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
