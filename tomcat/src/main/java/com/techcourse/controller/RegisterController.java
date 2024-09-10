package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.session.SessionManager;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.servlet.RestController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

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

        String jSessionId = SessionManager.addUser(user);
        response.addSessionToCookies(jSessionId);

        return redirectTo(response, "/index");
    }
}
