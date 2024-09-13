package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http.HeaderName;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.manager.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager;

    public LoginController() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.hasQueryParam()) {
            login(request, response, sessionManager);
        }
        if (!request.hasQueryParam()) {
            showLogin(request, response, sessionManager);
        }
    }

    private static void login(HttpRequest request, HttpResponse response, SessionManager sessionManager) {
        String account = request.getQueryParam("account");
        String password = request.getQueryParam("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.setBody("/401.html");
        }
        if (user.isPresent() && user.get().checkPassword(password)) {
            response.setStatusCode(StatusCode.FOUND);
            response.setBody("/index.html");

            String sessionId = sessionManager.generateSession(user.get());
            response.addHeader(HeaderName.SET_COOKIE, sessionId);
        }
    }

    private static void showLogin(HttpRequest request, HttpResponse response, SessionManager sessionManager) {
        if (request.hasSession() && sessionManager.isSessionExist(request.getSessionId())) {
            response.setStatusCode(StatusCode.FOUND); //
            response.addHeader(HeaderName.LOCATION, "/index.html");
        }
        if (!request.hasSession() || !sessionManager.isSessionExist(request.getSessionId())) {
            response.setStatusCode(StatusCode.OK);
            response.setBody("/login.html");
        }
    }
}
