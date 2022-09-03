package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import nextstep.jwp.servlet.handler.RequestMapping;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.request.Parameters;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpMethod;

public class PostController {

    private final UserService userService;

    public PostController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = HttpMethod.POST, path = "/login")
    public HttpResponse login(HttpRequest request) {
        Parameters parameters = request.getParameters();
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        return userService.findUser(account, password);
    }

    @RequestMapping(method = HttpMethod.POST, path = "/register")
    public HttpResponse register(HttpRequest request) {
        Parameters parameters = request.getParameters();
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");
        return userService.saveUser(account, password, email);
    }
}
