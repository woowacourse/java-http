package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
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
    protected void doGet(HttpRequest request, HttpResponse response) {
        request.getSessionFromCookies()
                .filter(SessionManager::contains)
                .ifPresentOrElse(
                        session -> redirectTo(response, "/index"),
                        () -> responseLoginPage(request, response)
                );
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        InMemoryUserRepository.findByAccount(request.getFromBody("account"))
                .filter(user -> user.checkPassword(request.getFromBody("password")))
                .ifPresentOrElse(
                        user -> loginAndRedirectToIndex(response, user),
                        () -> responsePage401(response)
                );
    }

    private void responseLoginPage(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.OK);
        responseResource(response, request.getURI());
    }

    private void loginAndRedirectToIndex(HttpResponse response, User user) {
        Session session = SessionManager.add("user", user);
        response.addSessionToCookies(session);
        redirectTo(response, "/index");
    }

    private void responsePage401(HttpResponse response) {
        String path = "/401.html";
        response.setStatus(HttpStatus.UNAUTHORIZED);
        responseResource(response, path);
    }
}
