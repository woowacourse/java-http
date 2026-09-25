package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> parameters = request.getParameters();
        if (canLogin(parameters)) {
            User user = InMemoryUserRepository.findByAccount(parameters.get("account")).get();
            Session session = request.getSession(true);
            session.setAttribute("user", user);

            response.sendRedirect("/index.html");
            response.addCookie(session.getId());
        } else {
            response.sendRedirect("/401.html");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.sendRedirect("/index.html");
        } else {
            response.forward(request.getPath());
        }
    }

    private boolean canLogin(Map<String, String> parameters) {
        String account = parameters.get("account");
        String password = parameters.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }


    private boolean isLoggedIn(HttpRequest request) {
        Session session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }
}
