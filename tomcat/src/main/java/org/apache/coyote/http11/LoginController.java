package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

final class LoginController extends AbstractController {
    private static final String USER_SESSION_ATTRIBUTE = "user";

    private final Controller staticResourceController;

    LoginController(final Controller staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        if (sessionFor(request).flatMap(this::userFor).isPresent()) {
            return ResponseFactory.redirect("/index.html", Optional.empty());
        }

        return staticResourceController.service(request);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final var user = authenticatedUser(request);

        if (user.isEmpty()) {
            return ResponseFactory.redirect("/401.html", request);
        }

        final var existingSessionId = ResponseFactory.sessionId(request);
        final var session = sessionFor(request).orElseGet(() -> SessionManager.create(
                existingSessionId.orElseGet(() -> UUID.randomUUID().toString())
        ));
        session.setAttribute(USER_SESSION_ATTRIBUTE, user.get());

        return ResponseFactory.redirect("/index.html", existingSessionId.isPresent()
                ? Optional.empty()
                : Optional.of(session.getId()));
    }

    private Optional<Session> sessionFor(final HttpRequest request) {
        return ResponseFactory.sessionId(request).flatMap(SessionManager::findSession);
    }

    private Optional<User> userFor(final Session session) {
        return Optional.ofNullable(session.getAttribute(USER_SESSION_ATTRIBUTE))
                .filter(User.class::isInstance)
                .map(User.class::cast);
    }

    private Optional<User> authenticatedUser(final HttpRequest request) {
        final var parameters = RequestUri.parseParameters(request.body());
        final var account = parameters.get("account");
        final var password = parameters.get("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
