package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {
    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        userService.register(request);
        response.redirect("/index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.ok("/register.html");
    }
}
