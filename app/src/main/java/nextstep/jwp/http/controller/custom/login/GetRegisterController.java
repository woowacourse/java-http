package nextstep.jwp.http.controller.custom.login;

import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.custom.CustomController;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Response;

public class GetRegisterController extends CustomController {

    private static final String REGISTER_PATH = "/register";
    private static final String REGISTER_RESOURCE_PATH = "/register.html";

    @Override
    public Response doService(HttpRequest httpRequest) {
        return HttpResponse.status(HttpStatus.OK, REGISTER_RESOURCE_PATH);
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.GET;
    }

    @Override
    protected String path() {
        return REGISTER_PATH;
    }
}
