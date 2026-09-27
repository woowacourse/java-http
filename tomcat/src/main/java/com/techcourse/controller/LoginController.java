package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.AbstractController;
import org.apache.catalina.StaticResource;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController {

    private static final String LOGIN_USER = "user";

    private final StaticResource staticResource;

    public LoginController(StaticResource staticResource) {
        this.staticResource = staticResource;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request.getSession(false))) {
            response.redirect("/index.html");
            return;
        }

        response.setContentType("text/html;charset=utf-8");
        response.setBody(staticResource.read("/login.html").orElse(new byte[0]));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        login(request, response);
    }

    private void login(HttpRequest request, HttpResponse response) {
        User user = findUser(request);
        if (user == null) {
            response.redirect("/401.html");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, user);
        response.redirect("/index.html");
    }

    private User findUser(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || password == null) {
            return null;
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElse(null);
    }

    private boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(LOGIN_USER) != null;
    }
}
