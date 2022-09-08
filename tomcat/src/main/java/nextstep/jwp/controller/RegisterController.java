package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return HttpResponse.createWithBody(HttpStatus.OK, request.getRequestLine());
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getBodyValue("account");
        String email = request.getBodyValue("email");
        String password = request.getBodyValue("password");
        userService.register(account, email, password);
        return HttpResponse.createWithoutBody(HttpStatus.FOUND, "/index");
    }
}
