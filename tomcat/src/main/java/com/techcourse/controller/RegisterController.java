package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.servlet.RestController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

@RequestMapping("/register")
public class RegisterController extends RestController {

    @Override
    protected boolean doGet(HttpRequest request, HttpResponse response) {
        return responseResource(response, request.getTargetPath());
    }

    @Override
    protected boolean doPost(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getFromBody("account"),
                request.getFromBody("password"),
                request.getFromBody("email")
        );
        InMemoryUserRepository.save(user);

        Session session = SessionManager.add("user", user);
        response.addSessionToCookies(session);

        return redirectTo(response, "/index");
    }
}
