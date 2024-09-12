package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT_FIELD_NAME = "account";
    private static final String EMAIL_FIELD_NAME = "email";
    private static final String PASSWORD_FIELD_NAME = "password";

    private static final String REGISTER_FILE_NAME = "register.html";
    private static final String INDEX_PATH = "/index.html";
    private static final String USER_ATTRIBUTE_NAME = "user";

    private final SessionManager sessionManager;

    public RegisterController() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getRequestBody();
        String account = requestBody.get(ACCOUNT_FIELD_NAME);
        String email = requestBody.get(EMAIL_FIELD_NAME);
        String password = requestBody.get(PASSWORD_FIELD_NAME);

        if (!InMemoryUserRepository.existsByAccount(account)) {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            String sessionId = saveSessionAndGetSessionId(user);
            response.setRedirectWithSessionId(INDEX_PATH, sessionId);
            return;
        }

        throw new UncheckedServletException("이미 존재하는 ID입니다.");
    }

    private String saveSessionAndGetSessionId(User user) {
        String sessionId = sessionManager.generateId();
        Session session = new Session();
        session.setAttribute(USER_ATTRIBUTE_NAME, user);
        sessionManager.add(sessionId, session);
        return sessionId;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStaticResource(REGISTER_FILE_NAME);
    }
}
