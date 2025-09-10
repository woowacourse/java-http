package org.apache.catalina.controller;

import static org.apache.coyote.http11.HttpConstants.CONTENT_LENGTH_HEADER;
import static org.apache.coyote.http11.HttpConstants.CONTENT_TYPE_HEADER;
import static org.apache.coyote.http11.HttpConstants.COOKIE_JSESSIONID;
import static org.apache.coyote.http11.HttpConstants.EQUAL;
import static org.apache.coyote.http11.HttpConstants.INDEX_PAGE;
import static org.apache.coyote.http11.HttpConstants.LOCATION_HEADER;
import static org.apache.coyote.http11.HttpConstants.LOGIN_PAGE;
import static org.apache.coyote.http11.HttpConstants.SET_COOKIE_HEADER;
import static org.apache.coyote.http11.HttpConstants.SLASH;
import static org.apache.coyote.http11.HttpConstants.UNAUTHORIZED_PAGE;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.dto.request.HttpRequest;
import org.apache.coyote.http11.dto.response.Status;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String ACCOUNT_PARAM = "account";
    private static final String PASSWORD_PARAM = "password";

    private final StaticResourceController staticResourceController;

    public LoginController(final StaticResourceController staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected ControllerResult doGet(final HttpRequest request) {
        return findSessionFromRequest(request)
                .map(session -> {
                    log.info("User already logged in with session '{}'", session.getId());
                    return redirectTo(INDEX_PAGE, session);
                })
                .orElseGet(() -> staticResourceController.serve(SLASH + LOGIN_PAGE));
    }

    @Override
    protected ControllerResult doPost(final HttpRequest request) {
        final Map<String, String> bodyQueryParam = request.getBodyQueryParam();
        final String account = bodyQueryParam.get(ACCOUNT_PARAM);
        final String password = bodyQueryParam.get(PASSWORD_PARAM);

        return validateUser(account, password)
                .map(user -> {
                    log.info("Login succeeded for account '{}'", user.getAccount());
                    final Session session = getOrCreateSession(request, user);
                    return redirectTo(INDEX_PAGE, session);
                })
                .orElseGet(this::serveUnauthorizedPage);
    }

    private Optional<User> validateUser(final String account, final String password) {
        if (account.isBlank() || password.isBlank()) {
            log.warn("Login attempt with missing credentials.");
            return Optional.empty();
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .or(() -> {
                    log.warn("Login failed for account '{}'. Invalid credentials.", account);
                    return Optional.empty();
                });
    }

    private Optional<Session> findSessionFromRequest(final HttpRequest request) {
        return Optional.ofNullable(request.getCookie(COOKIE_JSESSIONID))
                .map(SessionManager.getInstance()::findSession);
    }

    private Session getOrCreateSession(final HttpRequest request, final User user) {
        final Session session = findSessionFromRequest(request)
                .orElseGet(() -> {
                    final Session newSession = new Session(UUID.randomUUID().toString());
                    log.info("New session '{}' created for user '{}'", newSession.getId(), user.getAccount());
                    return newSession;
                });

        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        return session;
    }

    private ControllerResult redirectTo(final String page, final Session session) {
        return ControllerResult.builder()
                .status(Status.FOUND) // 302 Found
                .header(LOCATION_HEADER, SLASH + page)
                .header(SET_COOKIE_HEADER, COOKIE_JSESSIONID + EQUAL + session.getId())
                .requireSession(true)
                .build();
    }

    private ControllerResult serveUnauthorizedPage() {
        final ControllerResult result = staticResourceController.serve(SLASH + UNAUTHORIZED_PAGE);
        return ControllerResult.builder()
                .status(Status.UNAUTHORIZED) // 401 Unauthorized
                .header(CONTENT_TYPE_HEADER, result.headers().get(CONTENT_TYPE_HEADER))
                .header(CONTENT_LENGTH_HEADER, result.headers().get(CONTENT_LENGTH_HEADER))
                .body(result.body())
                .build();
    }
}
