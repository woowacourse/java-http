package nextstep.jwp.controller;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.UNAUTHORIZED;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        doGet(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Optional<User> optionalUser = extractUser(request);

        if (optionalUser.isEmpty()) {
            response.setStatus(UNAUTHORIZED);
            response.setBody("/401.html");
            return;
        }

        User user = optionalUser.get();
        log.info("user: {}", user);
        Session session = new Session(UUID.randomUUID().toString());
        session.addUser(user);
        SessionManager.add(session);

        response.setStatus(FOUND);
        response.sendRedirect("/index.html");
        response.setCookie(HttpCookie.from(session));
    }

    private Optional<User> extractUser(final HttpRequest request) {
        final var form = request.getForm();
        final var account = form.get("account");
        final var password = form.get("password");
        return findUser(account, password);
    }

    private Optional<User> findUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .stream().findFirst();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (isAlreadyLoggedIn(request)) {
            response.setStatus(FOUND);
            response.sendRedirect("/index.html");
            return;
        }
        response.setStatus(HttpStatus.OK);
        response.setBody("/login.html");
    }

    private boolean isAlreadyLoggedIn(final HttpRequest request) {
        String sessionId = request.getCookie("JSESSIONID");
        return SessionManager.findSession(sessionId) != null;
    }

}
