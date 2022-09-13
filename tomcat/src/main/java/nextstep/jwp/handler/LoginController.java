package nextstep.jwp.handler;

import java.time.LocalDateTime;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.HandlerResponseEntity;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends HttpRequestHandler {

    private static final LoginController instance = new LoginController();
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    private final SessionManager sessionManager = new SessionManager();

    private LoginController() {}

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    public HandlerResponseEntity doGet(final HttpRequest httpRequest, final HttpResponseHeader responseHeader) {
        final Session session = httpRequest.getSession();
        if (sessionManager.contains(session.getId())) {
            return HandlerResponseEntity.createRedirectResponse(HttpStatus.FOUND, responseHeader, "/index.html");
        }
        return HandlerResponseEntity.createWithResource("/login.html");
    }

    @Override
    public HandlerResponseEntity doPost(final HttpRequest request, final HttpResponseHeader responseHeader) {
        validateQueryParams(request);

        InMemoryUserRepository.findByAccount(request.getParameter(ACCOUNT_KEY))
                .ifPresentOrElse(it -> login(it, request, responseHeader), () -> {
                    throw new IllegalArgumentException("User not found");
                });

        return HandlerResponseEntity.createRedirectResponse(HttpStatus.FOUND, responseHeader, "/index.html");
    }

    private void validateQueryParams(final HttpRequest request) {
        if (!request.containsParameter(ACCOUNT_KEY) || !request.containsParameter(PASSWORD_KEY)) {
            throw new IllegalArgumentException("No Parameters");
        }
    }

    private void login(final User user, final HttpRequest httpRequest, final HttpResponseHeader responseHeader) {
        validatePassword(user, httpRequest.getParameter(PASSWORD_KEY));
        log.info(user.toString());

        handleSession(user, httpRequest, responseHeader);
    }

    private void validatePassword(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("User not found");
        }
    }

    private void handleSession(final User user, final HttpRequest httpRequest,
                               final HttpResponseHeader responseHeader) {
        final Session session = httpRequest.getSession();
        session.addAttribute("user", user);
        sessionManager.add(session);
        responseHeader.addCookie("JSESSIONID", session.getId());
    }
}
