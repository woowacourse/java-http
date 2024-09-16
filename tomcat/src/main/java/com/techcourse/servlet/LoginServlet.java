package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.line.HttpStatus;

public class LoginServlet extends HttpServlet {

    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String PAGE_RESOURCE_PATH = "static/login.html";
    private static final String LOGIN_FAIL_PAGE = "static/401.html";
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String ACCOUNT_FORM_DATA = "account";
    private static final String PASSWORD_FORM_DATA = "password";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (sessionManager.hasSession(request.getSessionId())) {
            response.sendRedirect(LOGIN_SUCCESS_REDIRECT_URI);
            return;
        }

        response.ok(PAGE_RESOURCE_PATH);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getFormData(ACCOUNT_FORM_DATA);
        String password = request.getFormData(PASSWORD_FORM_DATA);

        InMemoryUserRepository.findByAccountAndPassword(account, password)
                .ifPresentOrElse(
                        user -> sendLoginSuccessResponse(response, user),
                        () -> response.sendError(HttpStatus.UNAUTHORIZED, LOGIN_FAIL_PAGE)
                );
    }

    private void sendLoginSuccessResponse(HttpResponse response, User loginedUser) {
        Session session = new Session();
        session.setAttributes("user", loginedUser);
        sessionManager.add(session);

        response.sendRedirect(LOGIN_SUCCESS_REDIRECT_URI);
        response.setJsession(session.getId());
    }
}
