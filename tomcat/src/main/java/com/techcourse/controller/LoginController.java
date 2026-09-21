package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.HttpCookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager;

    public LoginController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    @Override
    protected String doGet(HttpRequest request, HttpResponse response) throws IOException {
        Session session = sessionManager.findSession(request.getCookie("JSESSIONID"));
        if (session != null && session.getAttribute("user") != null) {
            return "redirect:/index.html";
        }
        return "/login.html";
    }

    @Override
    protected String doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getBodyValue("account");
        String password = request.getBodyValue("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(it -> it.checkPassword(password));
        if (user.isEmpty()) {
            return "redirect:/401.html";
        }

        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user.get());
        sessionManager.add(session);
        response.setCookie(new HttpCookie("JSESSIONID=" + session.getId()));
        return "redirect:/index.html";
    }

}
