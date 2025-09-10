package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.Http11Processor;
import org.apache.http.Cookie;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.apache.http.value.HttpMethod;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        return request.getMethod() == HttpMethod.POST
                && request.getUri().equals("/login");
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
        String account = request.getBody("account");
        String password = request.getBody("password");

        Optional<User> user = getUser(account);

        boolean isAuthenticated = isAuthenticatedUser(user, password);
        if (!isAuthenticated) {
            response.setRedirection("/401.html");
            return;
        }

        String sessionId = makeSession(user.get());
        response.setRedirection("/index.html");
        response.setCookie(Cookie.makeSessionCookie(sessionId));
    }

    private Optional<User> getUser(String account) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        user.ifPresent(value -> log.info("user : {}", value.toString()));
        return user;
    }

    private boolean isAuthenticatedUser(Optional<User> user, String password) {
        return user.isPresent() && user.get().checkPassword(password);
    }

    private String makeSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        SessionManager.add(new Session(sessionId, user));
        return sessionId;
    }
}
