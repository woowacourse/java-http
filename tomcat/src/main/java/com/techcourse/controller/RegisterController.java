package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private static final String REGISTER_FILE_NAME = "register.html";
    private static final String INDEX_PATH = "/index.html";

    private final SessionManager sessionManager;

    public RegisterController() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getRequestBody();
        validateFields(requestBody);

        String account = requestBody.get(ACCOUNT);
        String email = requestBody.get(EMAIL);
        String password = requestBody.get(PASSWORD);

        if (!InMemoryUserRepository.existsByAccount(account)) {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            String sessionId = sessionManager.generateId();
            sessionManager.add(sessionId, Session.ofUser(user));
            response.setRedirect(INDEX_PATH);
            response.setCookie(HttpCookie.ofSessionId(sessionId));
        }

        throw new UncheckedServletException("이미 존재하는 ID입니다.");
    }

    private void validateFields(RequestBody requestBody) {
        if (!requestBody.containsExactly(ACCOUNT, EMAIL, PASSWORD)) {
            throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStaticResource(REGISTER_FILE_NAME);
    }
}
