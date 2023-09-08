package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.controllermapping.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resource.FileHandler;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestMapping(uri = "/login")
public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> requestBody = request.getRequestBodyAsMap();
        final Optional<String> account = Optional.ofNullable(requestBody.get("account"));
        final Optional<String> password = Optional.ofNullable(requestBody.get("password"));

        if (account.isEmpty() || password.isEmpty()) {
            returnUnauthorizedPage(response);
            return;
        }
        verifyAccount(account.get(), password.get(), response);
    }

    private void verifyAccount(String account, String password, HttpResponse response) throws IOException {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            returnUnauthorizedPage(response);
            return;
        }
        logAccount(user.get());
        redirectIndexPage(response, user.get());
    }

    private void returnUnauthorizedPage(HttpResponse response) throws IOException {
        response.setCharSet("utf-8");
        response.setContentType("text/html");
        response.setResponseStatus("401");
        response.setResponseBody(new FileHandler().readFromResourcePath("static/401.html"));
        response.flush();
    }

    private void redirectIndexPage(HttpResponse response, User user) throws IOException {
        final String sessionId = UUID.randomUUID().toString();
        response.addCookie("JSESSIONID", sessionId);
        saveUserToSession(user, sessionId);

        response.sendRedirect("/index.html");
    }

    private void saveUserToSession(User user, String sessionId) {
        final Session session = new Session(sessionId);
        session.setAttribute(sessionId, user);
        SessionManager.add(session);
    }

    private void logAccount(User user) {
        log.info("user : {}", user);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Optional<String> sessionId = request.getSessionId();
        if (sessionId.isPresent() && SessionManager.hasSessionWithAttributeType(sessionId.get(), User.class)) {
            response.sendRedirect("/index.html");
        }

        response.setCharSet("utf-8");
        response.setContentType("text/html");
        response.setResponseStatus("200");
        response.setResponseBody(new FileHandler().readFromResourcePath("static/login.html"));
        response.flush();
    }
}
