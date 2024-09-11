package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Objects;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.session.Session;

public class LoginController extends AbstractController {

    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";

    private static final String ACCOUNT_FIELD = "account";
    private static final String PASSWORD_FIELD = "password";
    private static final String USER_FIELD = "user";

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String account = getAccountFromRequest(request);
        String password = getPasswordFromRequest(request);
        User user = getUser(account, password);
        request.getSession().setAttribute(USER_FIELD, user);
        response.sendRedirect(INDEX_PAGE);
        response.setHeader(Header.SET_COOKIE.value(), "JSESSIONID=" + request.getSession().getId());
    }

    private String getAccountFromRequest(HttpRequest request) {
        return getRequiredBodyField(request, ACCOUNT_FIELD);
    }

    private String getPasswordFromRequest(HttpRequest request) {
        return getRequiredBodyField(request, PASSWORD_FIELD);
    }

    private User getUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않거나 비밀번호가 일치하지 않습니다."));
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (userLoggedIn(request)) {
            response.sendRedirect(INDEX_PAGE);
            return;
        }
        response.setBody(readStaticResource(LOGIN_PAGE));
    }

    private boolean userLoggedIn(HttpRequest request) {
        Session session = request.getSession();
        Object user = session.getAttribute(USER_FIELD);
        if (user == null) {
            return false;
        }
        return InMemoryUserRepository.findByAccount(((User) user).getAccount())
                .isPresent();
    }
}
