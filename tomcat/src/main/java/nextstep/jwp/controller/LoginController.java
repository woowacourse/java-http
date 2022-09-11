package nextstep.jwp.controller;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        try {
            final User user = userService.login(request);
            final String jSessionId = UUID.randomUUID().toString();
            final Session session = new Session(jSessionId);
            SessionManager.add(session);
            session.setAttribute("user", user);

            response.setStatus(StatusCode.FOUND);
            response.setHeaders("/index.html", jSessionId);
        } catch (final IllegalArgumentException e) {
            response.redirect("/401.html");
        }
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final Optional<String> jSessionId = request.findCookie("JSESSIONID");
        if (jSessionId.isPresent() && SessionManager.findSession(jSessionId.get()).isPresent()) {
            response.redirect("/index.html");
            return;
        }
        response.ok("/login.html");
    }
}
