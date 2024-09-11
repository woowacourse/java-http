package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.startline.HttpStatus;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.servlet.RestController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

@RequestMapping("/login")
public class LoginController extends RestController {

    @Override
    protected boolean doGet(HttpRequest request, HttpResponse response) {
        return request.getSessionFromCookies()
                .filter(SessionManager::contains)
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
        Session session = SessionManager.add("user", user);
        response.addSessionToCookies(session);
        return redirectTo(response, "/index");
    }

    private boolean responsePage401(HttpResponse response) {
        String path = "/401.html";
        response.setStatus(HttpStatus.UNAUTHORIZED);
        return responseResource(response, path);
    }
}
