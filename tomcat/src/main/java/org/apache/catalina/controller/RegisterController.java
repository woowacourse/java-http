package org.apache.catalina.controller;

import static org.apache.coyote.http11.HttpConstants.CONTENT_LENGTH_HEADER;
import static org.apache.coyote.http11.HttpConstants.CONTENT_TYPE_HEADER;
import static org.apache.coyote.http11.HttpConstants.COOKIE_JSESSIONID;
import static org.apache.coyote.http11.HttpConstants.EQUAL;
import static org.apache.coyote.http11.HttpConstants.INDEX_PAGE;
import static org.apache.coyote.http11.HttpConstants.LOCATION_HEADER;
import static org.apache.coyote.http11.HttpConstants.REGISTER_PAGE;
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

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String ACCOUNT_PARAM = "account";
    private static final String PASSWORD_PARAM = "password";
    private static final String EMAIL_PARAM = "email";

    private final StaticResourceController staticResourceController;

    public RegisterController(final StaticResourceController staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected ControllerResult doGet(final HttpRequest request) {
        return staticResourceController.serve(SLASH + REGISTER_PAGE);
    }

    @Override
    protected ControllerResult doPost(final HttpRequest request) {
        final Map<String, String> bodyQueryParam = request.getBodyQueryParam();
        final String account = bodyQueryParam.get(ACCOUNT_PARAM);
        final String password = bodyQueryParam.get(PASSWORD_PARAM);
        final String email = bodyQueryParam.get(EMAIL_PARAM);

        return validateAndBuildUser(account, password, email)
                .map(this::registerUserAndCreateSession)
                .map(session -> redirectTo(INDEX_PAGE, session))
                .orElseGet(this::serveUnauthorizedPage);
    }

    private Optional<User> validateAndBuildUser(final String account, final String password, final String email) {
        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            log.warn("Register attempt with missing credentials.");
            return Optional.empty();
        }
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.warn("Register failed for account '{}' - already exists.", account);
            return Optional.empty();
        }
        return Optional.of(new User(account, password, email));
    }

    private Session registerUserAndCreateSession(final User user) {
        InMemoryUserRepository.save(user);
        log.info("Register succeeded for account '{}'", user.getAccount());

        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        log.info("New session '{}' created for user '{}'", session.getId(), user.getAccount());

        return session;
    }

    private ControllerResult redirectTo(final String page, final Session session) {
        return ControllerResult.builder()
                .status(Status.FOUND) // 302 Found
                .header(LOCATION_HEADER, SLASH + page)
                .header(SET_COOKIE_HEADER, COOKIE_JSESSIONID + EQUAL + session.getId())
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
