package nextstep.jwp.web.presentation.controller.login;

import nextstep.jwp.http.header.element.HttpStatus;
import nextstep.jwp.web.presentation.controller.CustomController;
import nextstep.jwp.http.header.request.HttpRequest;
import nextstep.jwp.http.header.request.request_line.HttpMethod;
import nextstep.jwp.http.header.response.HttpResponse;
import nextstep.jwp.http.header.response.Response;

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
