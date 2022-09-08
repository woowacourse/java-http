package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RegisterController extends AbstractController {
    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        userService.register(request);
        response.redirect(request, "/index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String responseBody = response.createResponseBody("/register.html");
        response.setBody(responseBody);
        response.setHeaders(ContentType.HTML);
        response.setStatus(request, StatusCode.OK);
    }
}
