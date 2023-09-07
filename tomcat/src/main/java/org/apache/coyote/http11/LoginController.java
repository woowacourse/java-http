package org.apache.coyote.http11;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.UNAUTHORIZED;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private ResourceResponseHandler resourceResponseHandler = new ResourceResponseHandler(); // todo: 지우기

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        doGet(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final var form = request.getForm();
        final var account = form.get("account");
        final var password = form.get("password");
        final var optionalUser = findUser(account, password);

        if (optionalUser.isEmpty()) {
            response.setStatus(UNAUTHORIZED);
            final var responseBody = resourceResponseHandler.buildBodyFrom("/401.html");
            response.setBody(responseBody);
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

    private Optional<User> findUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .stream().findFirst();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isAlreadyLoggedIn(request)) {
            response.setStatus(FOUND);
            response.sendRedirect("/index.html");
            return;
        }
        final var responseBody = resourceResponseHandler.buildBodyFrom("/login.html");
        response.setStatus(HttpStatus.OK);
        response.setBody(responseBody);
    }

    private boolean isAlreadyLoggedIn(final HttpRequest request) {
        String sessionId = request.getCookie("JSESSIONID");
        return SessionManager.findSession(sessionId) != null;
    }

}
