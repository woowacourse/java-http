package nextstep.web;

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
        return new View("login.html");
    }

    @Override
    public View handlePostRequest(final HttpRequest request, final HttpResponse response) {
        final String account = request.getPayloadValue("account");
        final String password = request.getPayloadValue("password");
        if (userService.validateLogin(account, password)) {
            User user = userService.getUserByAccount(account);
            log.info("login success: {}", user);
            return redirect("/index.html", response);
        }

        return new View("401.html");
    }
}
