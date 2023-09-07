package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.UNAUTHORIZED;

public class RequestHandler {

    private final ResourceResponseHandler resourceResponseHandler;

    public RequestHandler() {
        this.resourceResponseHandler = new ResourceResponseHandler();
    }

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public HttpResponse handle(final HttpRequest request) throws IOException {
        final var uri = request.getPath().getValue();
        if (uri.equals("/login")) {
            return handleLogin(request);
        }
        if (uri.equals("/register")) {
            return handleRegister(request);
        }
        return resourceResponseHandler.handleStaticResponse(uri);
    }

    private HttpResponse handleLogin(final HttpRequest request) {
        if (request.isPost()) {
            return postLogin(request);
        }
        if (isAlreadyLoggedIn(request)) {
            return HttpResponse.builder()
                    .setHttpVersion(request.getVersion())
                    .setHttpStatus(FOUND)
                    .sendRedirect("/index.html")
                    .build();
        }
        return resourceResponseHandler.handleStaticResponse("/login.html");
    }

    private HttpResponse handleRegister(final HttpRequest request) {
        if (request.isPost()) {
            return postRegister(request);
        }
        return resourceResponseHandler.handleStaticResponse("/register.html");
    }

    private HttpResponse postLogin(HttpRequest request) {
        final var form = request.getForm();
        final var account = form.get("account");
        final var password = form.get("password");
        final var optionalUser = findUser(account, password);

        if (optionalUser.isEmpty()) {
            return resourceResponseHandler.handleStaticResponse(UNAUTHORIZED, "/401.html");
        }

        User user = optionalUser.get();
        log.info("user: {}", user);

        Session session = new Session(UUID.randomUUID().toString());
        session.addUser(user);

        SessionManager.add(session);
        return HttpResponse.builder()
                .setHttpVersion(request.getVersion())
                .setHttpStatus(FOUND)
                .sendRedirect("/index.html")
                .addCookie(HttpCookie.from(session))
                .build();
    }

    private Optional<User> findUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .stream().findFirst();
    }

    private boolean isAlreadyLoggedIn(final HttpRequest request) {
        String sessionId = request.getCookie("JSESSIONID");
        return SessionManager.findSession(sessionId) != null;
    }

    private HttpResponse postRegister(HttpRequest request) {
        final var form = request.getForm();
        final var account = form.get("account");
        final var password = form.get("password");
        final var email = form.get("email");
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.builder()
                .setHttpVersion(request.getVersion())
                .setHttpStatus(FOUND)
                .sendRedirect("/index.html")
                .build();
    }

}
