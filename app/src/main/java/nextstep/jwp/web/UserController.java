package nextstep.jwp.web;

import static nextstep.jwp.http.HttpResponse.found;
import static nextstep.jwp.http.HttpResponse.redirect;
import static nextstep.jwp.http.HttpResponse.unauthorized;
import static nextstep.jwp.http.ViewResolver.resolveView;

import java.io.IOException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.RequestParam;

public class UserController {
    private final UserService userService = new UserService();

    public String login() throws IOException {
        return resolveView("login");
    }

    public String login(RequestParam params) {
        try {
            userService.login(params.get("account"), params.get("password"));
        } catch (UnauthorizedException e) {
            return unauthorized();
        }
        return found("/index.html");
    }

    public String register() throws IOException {
        return resolveView("register");
    }

    public String register(RequestParam params) {
        userService.save(params.get("account"), params.get("password"), params.get("email"));
        return redirect("/index.html");
    }
}
