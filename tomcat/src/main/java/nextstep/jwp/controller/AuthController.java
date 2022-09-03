package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.Parameters;
import org.apache.coyote.response.HttpResponse;

public class AuthController {

    private final UserService userService = new UserService();

    public HttpResponse login(HttpRequest request) {
        Parameters parameters = request.getParameters();
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        return userService.findUser(account, password);
    }
}
