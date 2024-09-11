package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_FIELD = "account";
    private static final String PASSWORD_FIELD = "password";

    private final SessionManager sessionManager;

    public LoginController() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getRequestBody();
        validateFields(requestBody);

        String account = requestBody.get(ACCOUNT_FIELD);
        String password = requestBody.get(PASSWORD_FIELD);

        if (InMemoryUserRepository.exists(account, password)) {
            User user = InMemoryUserRepository.getByAccount(account);
            String sessionId = sessionManager.generateId();
            sessionManager.add(sessionId, Session.ofUser(user));
            response.setRedirect("/index.html");
            response.setCookie(HttpCookie.ofSessionId(sessionId));
            return;
        }

        response.setUnauthorized();
    }

    private static void validateFields(RequestBody requestBody) {
        if (!requestBody.containsExactly(ACCOUNT_FIELD, PASSWORD_FIELD)) {
            throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.hasSession() && sessionManager.hasId(request.getSession())) {
            response.setRedirect("/index.html");
            return;
        }

        response.setStaticResource("login.html");
    }
}
