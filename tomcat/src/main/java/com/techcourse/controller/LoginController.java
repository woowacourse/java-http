package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.session.SessionManager;
import java.util.Optional;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.servlet.RestController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.startline.HttpStatus;

@RequestMapping("/login")
public class LoginController extends RestController {

    @Override
    protected boolean doGet(HttpRequest request, HttpResponse response) {
        return request.getSessionFromCookie()
                .map(session -> redirectTo(response, "/index"))
                .orElseGet(() -> responseResource(response, request.getTargetPath()));
    }

    @Override
    protected boolean doPost(HttpRequest request, HttpResponse response) {
        Optional<User> nullableUser = InMemoryUserRepository.findByAccount(request.getFromBody("account"));
        return nullableUser
                .filter(user -> user.checkPassword(request.getFromBody("password")))
                .map(user -> loginAndRedirectToIndex(response, user))
                .orElseGet(() -> responsePage401(response));
    }

    private boolean loginAndRedirectToIndex(HttpResponse response, User user) {
        String jSessionId = SessionManager.addUser(user);
        response.addSessionToCookies(jSessionId);
        redirectTo(response, "/index");
        return response.isValid();
    }

    private boolean responsePage401(HttpResponse response) {
        String path = "static/401.html";
        response.setStatus(HttpStatus.UNAUTHORIZED);

        return responseResource(response, path);
    }
}
