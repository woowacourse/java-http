package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;
import org.apache.util.FileUtils;

public class LoginServlet implements Servlet {

    private static final String LOGIN_FAIL_PAGE = "/401.html";
    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String ACCOUNT_FORM_DATA = "account";
    private static final String PASSWORD_FORM_DATA = "password";

    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
