package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return HttpResponseBuilder.ok(request);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        final String account = request.getBodyValue("account");
        final String email = request.getBodyValue("email");
        final String password = request.getBodyValue("password");
        userService.register(account, email, password);

        return HttpResponseBuilder.found("/index");
    }
}
