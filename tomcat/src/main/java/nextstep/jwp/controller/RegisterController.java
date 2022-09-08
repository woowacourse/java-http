package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return HttpResponseBuilder.ok(request);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        return HttpResponseBuilder.found("/index");
    }
}
