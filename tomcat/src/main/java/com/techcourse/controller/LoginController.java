package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_FIELD_NAME = "account";
    private static final String PASSWORD_FIELD_NAME = "password";
    private static final String USER_ATTRIBUTE_NAME = "user";

    private static final String INDEX_PATH = "/index.html";
    private static final String INDEX_FILE_NAME = "login.html";

    private final SessionManager sessionManager;

    public LoginController() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getRequestBody();
        String account = requestBody.get(ACCOUNT_FIELD_NAME);
        String password = requestBody.get(PASSWORD_FIELD_NAME);

        if (InMemoryUserRepository.exists(account, password)) {
            User user = InMemoryUserRepository.getByAccount(account);
            String sessionId = saveSessionAndGetSessionId(user);
            response.setRedirectWithSessionId(INDEX_PATH, sessionId);
            return;
        }

        response.setUnauthorized();
    }

    private String saveSessionAndGetSessionId(User user) {
        Session session = new Session();
        session.setAttribute(USER_ATTRIBUTE_NAME, user);

        String sessionId = sessionManager.generateId();
        sessionManager.add(sessionId, session);

        return sessionId;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.hasSession() && sessionManager.hasId(request.getSession())) {
            response.setRedirect(INDEX_PATH);
            return;
        }

        response.setStaticResource(INDEX_FILE_NAME);
    }
}
