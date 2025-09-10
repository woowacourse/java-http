package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        handleLogin(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if(isLoggedIn(request)) {
            response.found("/index.html");
            return;
        }
        response.ok("/login.html");
    }

    private void handleLogin(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        Map<String, String> requestBody = request.getRequestBody();
        if(requestBody.isEmpty()) {
            log.info("login failed: queryString is empty");
            response.found("/401.html");
            return;
        }
        if(isLoggedIn(request)) {
            log.info("login successful: already logged in");
            response.ok("/index.html");
            return;
        }
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(requestBody.get("account"));
        if(userOptional.isPresent() && userOptional.get().checkPassword(requestBody.get("password"))) {
            loginInSession(userOptional.get(), response);
            return;
        }
        log.info("login failed: invalid user info");
        response.found("/401.html");
    }

    private void loginInSession(final User user, final HttpResponse response) {
        log.info("User{}", user);
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        session.setAttribute("user", user);
        response.addCookie("JSESSIONID=" + session.getId());
        response.found("/index.html");
        log.info("login successful: logged in");
    }

    private boolean isLoggedIn(HttpRequest request) {
        HttpCookie httpCookie = request.getHttpCookie();
        if (httpCookie.hasNoSession()) {
            return false;
        }
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(httpCookie.getSessionId());
        if (session == null) {
            return false;
        }
        User user = getUser(session);
        return user != null && InMemoryUserRepository.findByAccount(user.getAccount()).isPresent();
    }

    private User getUser(final Session session) {
        return (User) session.getAttribute("user");
    }
}
