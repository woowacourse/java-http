package nextstep.jwp.web.presentation.controller.login;

import nextstep.jwp.http.message.element.HttpStatus;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.http.message.response.HttpResponse;
import nextstep.jwp.http.message.response.Response;
import nextstep.jwp.web.presentation.controller.CustomController;

public class GetLoginController extends CustomController {

    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE_RESOURCE_PATH = "/login.html";
    
    @Override
    public Response doService(HttpRequest httpRequest) {
        return HttpResponse.status(HttpStatus.OK, LOGIN_PAGE_RESOURCE_PATH);
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.GET;
    }

    @Override
    protected String path() {
        return LOGIN_PATH;
    }
}
