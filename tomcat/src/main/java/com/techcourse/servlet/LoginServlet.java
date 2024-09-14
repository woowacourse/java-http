package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;
import org.apache.util.FileUtils;

public class LoginServlet extends HttpServlet {

    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String PAGE_RESOURCE_PATH = "static/login.html";
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String LOGIN_FAIL_PAGE = "static/401.html";
    private static final String ACCOUNT_FORM_DATA = "account";
    private static final String PASSWORD_FORM_DATA = "password";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (sessionManager.hasSession(request.getSessionId())) {
            response.redirect(LOGIN_SUCCESS_REDIRECT_URI);
            return;
        }

        String fileContent = FileUtils.readFile(PAGE_RESOURCE_PATH);
        response.ok(fileContent, "html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String account = request.getFormData(ACCOUNT_FORM_DATA);
        String password = request.getFormData(PASSWORD_FORM_DATA);

        Optional<User> found = InMemoryUserRepository.findByAccount(account);
        if (found.isEmpty() || !found.get().checkPassword(password)) {
            response.unauthorized(FileUtils.readFile(LOGIN_FAIL_PAGE), "html");
            return;
        }

        Session session = new Session();
        session.setAttributes("user", found.get());
        sessionManager.add(session);

        response.redirect(LOGIN_SUCCESS_REDIRECT_URI);
        response.setJsession(session.getId());
    }
}
