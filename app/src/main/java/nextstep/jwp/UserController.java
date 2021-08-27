package nextstep.jwp;

import static nextstep.jwp.http.HttpResponse.found;
import static nextstep.jwp.http.HttpResponse.redirect;
import static nextstep.jwp.http.HttpResponse.unauthorized;
import static nextstep.jwp.http.ViewResolver.resolveView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.RequestParam;
import nextstep.jwp.model.User;

public class UserController {
    private final UserService userService = new UserService();

    public String login() throws IOException {
        return resolveView("login");
    }

    public String login(RequestParam params) {
        try {
            userService.login(params.get("account"), params.get("password"));
        } catch (IllegalArgumentException e) {
            return unauthorized();
        }
        return found("/index.html");
    }

    public String register() throws IOException {
        return resolveView("register");
    }

    public String register(RequestParam params) throws IOException {
        userService.save(params.get("account"), params.get("password"), params.get("email"));
        return redirect("/index.html");
    }
}
