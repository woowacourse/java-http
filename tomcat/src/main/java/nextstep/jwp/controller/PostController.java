package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.request.Parameters;
import org.apache.coyote.servlet.response.HttpResponse;

public class PostController {

    private final UserService userService;

    public PostController(UserService userService) {
        this.userService = userService;
    }

    public HttpResponse login(HttpRequest request) {
        Parameters parameters = request.getParameters();
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        return userService.findUser(account, password);
    }

    public HttpResponse register(HttpRequest request) {
        Parameters parameters = request.getParameters();
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");
        return userService.saveUser(account, password, email);
    }
}
