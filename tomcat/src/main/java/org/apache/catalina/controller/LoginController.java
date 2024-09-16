package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.HeaderName;
import org.apache.catalina.http.StatusCode;
import org.apache.catalina.manager.SessionManager;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager;

    public LoginController() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBodyParam("account");
        String password = request.getBodyParam("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.setBody("/401.html");
        }
        if (user.isPresent() && user.get().checkPassword(password)) {
            response.setStatusCode(StatusCode.FOUND);
            response.setBody("/index.html");
            login(response, user.get());
        }
    }

    private void login(HttpResponse response, User user) {
        String sessionId = sessionManager.generateSession(user);
        response.addHeader(HeaderName.SET_COOKIE, "JSESSIONID=" + sessionId);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (hasLogined(request)) {
            response.setStatusCode(StatusCode.FOUND); //
            response.addHeader(HeaderName.LOCATION, "/index.html");
        }
        if (!hasLogined(request)) {
            response.setStatusCode(StatusCode.OK);
            response.setBody("/login.html");
        }
    }

    private boolean hasLogined(HttpRequest request) {
        return request.hasSession() && sessionManager.isSessionExist(request.getSessionId());
    }
}
