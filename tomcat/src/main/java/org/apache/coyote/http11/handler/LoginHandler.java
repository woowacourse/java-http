package org.apache.coyote.http11.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.*;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean support(HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("POST") && httpRequest.isUriEqualTo("/login");
    }

    @Override
    public void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        final Map<String, String> requestBody = httpRequest.getRequestBodyAsMap();
        final Optional<String> account = Optional.ofNullable(requestBody.get("account"));
        final Optional<String> password = Optional.ofNullable(requestBody.get("password"));

        if (account.isEmpty() || password.isEmpty()) {
            returnUnauthorizedPage(outputStream);
            return;
        }
        verifyAccount(account.get(), password.get(), outputStream);
    }

    private void verifyAccount(String account, String password, OutputStream outputStream) throws IOException {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            returnUnauthorizedPage(outputStream);
            return;
        }
        logAccount(user.get());
        returnIndexPage(outputStream, user.get());
    }

    private void returnUnauthorizedPage(OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(new FileHandler().readFromResourcePath("static/401.html"))
                .responseStatus("401")
                .build(outputStream);
        httpResponse.flush();
    }

    private void returnIndexPage(OutputStream outputStream, User user) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(new FileHandler().readFromResourcePath("static/index.html"))
                .build(outputStream);

        final String sessionId = UUID.randomUUID().toString();
        httpResponse.addCookie("JSESSIONID", sessionId);
        saveUserToSession(user, sessionId);

        httpResponse.flush();
    }

    private void saveUserToSession(User user, String sessionId) {
        final Session session = new Session(sessionId);
        session.setAttribute(sessionId, user);
        SessionManager.add(session);
    }

    private void logAccount(User user) {
        log.info("user : {}", user);
    }
}
