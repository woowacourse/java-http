package nextstep.jwp.controller;

import static org.apache.coyote.response.ContentType.HTML;
import static org.apache.coyote.response.StatusCode.FOUND;
import static org.apache.coyote.response.StatusCode.OK;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.query.QueryParams;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.Location;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final QueryParams queryParams = request.getQueryParams();
        final String account = queryParams.getValueFromKey("account");
        final String password = queryParams.getValueFromKey("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchUserException::new);

        if (!user.checkPassword(password)) {
            throw new NoSuchUserException();
        }
        log.info("User : {}", user);
        final Session session = saveUserInSession(user);
        loginWithSuccessResponse(request, response, session);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final Optional<Cookie> optionalCookie = request.getJSessionCookie();
        if (optionalCookie.isPresent()) {
            final Cookie cookie = optionalCookie.get();
            handleSession(cookie);

            response.setResponse(FOUND, HTML, Location.from("/index.html"));
            response.print();
            return;
        }

        response.setResponse(OK, HTML, "/login.html");
        response.print();
    }

    private static Session saveUserInSession(User user) {
        final Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);

        log.info("sessionId : {}", session.getId());

        return session;
    }

    private static void loginWithSuccessResponse(HttpRequest request, HttpResponse response, Session session) {
        final Optional<Cookie> cookie = request.getJSessionCookie();
        if (cookie.isEmpty()) {
            final Cookie jsessionid = Cookie.ofJSessionId(session.getId());

            response.setResponse(FOUND, HTML, Location.from("/index.html"), jsessionid);
            response.print();
            return;
        }

        response.setResponse(FOUND, HTML, Location.from("/index.html"));
    }

    private static void handleSession(Cookie cookie) {
        final Session session = SessionManager.findSession(cookie.getValue());
        final User user = (User) session.getAttribute("user");
        log.info("User : {}", user);
    }
}
