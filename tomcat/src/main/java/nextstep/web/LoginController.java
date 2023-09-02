package nextstep.web;

import java.util.Objects;
import nextstep.jwp.application.UserService;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.web.AbstractController;
import org.apache.coyote.http11.web.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService = new UserService();

    @Override
    public View handleGetRequest(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParam("account");
        final String password = request.getParam("password");
        if (Objects.nonNull(account) && Objects.nonNull(password)) {
            return login(response, account, password);
        }

        return new View("login.html");
    }

    private View login(final HttpResponse response, final String account, final String password) {
        if (userService.validateLogin(account, password)) {
            User user = userService.getUserByAccount(account);
            log.info("login success: {}", user);
            return redirect("/index.html", response);
        }

        return new View("401.html");
    }

    @Override
    public View handlePostRequest(final HttpRequest request, final HttpResponse response) {
        return new View("login.html");
    }
}
