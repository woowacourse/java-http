package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11ResourceFinder;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11StatusCode;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final Http11ResourceFinder resourceFinder = new Http11ResourceFinder();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        boolean alreadyLogin = request.findSessionCookie().map(Http11Cookie::value).map(SESSION_MANAGER::findSession)
                .map(session -> session.getAttribute("account"))
                .isPresent();
        if (alreadyLogin) {
            response.setRedirect("/index.html");
            return;
        }
        Path path = resourceFinder.find(request.requestUri());
        response.setBodyAndContentType(path);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Optional<Http11Cookie> sessionCookie = request.findSessionCookie();
        if (sessionCookie.isEmpty()) {
            return;
        }

        String sessionId = sessionCookie.get().value();
        Session session = SESSION_MANAGER.findSession(sessionId);
        if (session == null) { // 쿠키를 만료시켜야 함
            SESSION_MANAGER.add(new Session(sessionId));
            session = SESSION_MANAGER.findSession(sessionId);
        }

        if (session.hasAttribute("user")) { // 로그인 되어있음
            Path path = resourceFinder.find("/index.html");
            response.setBodyAndContentType(path);
            response.setStatusCode(Http11StatusCode.FOUND);
            return;
        }

        LinkedHashMap<String, String> requestBody = request.body();
        String account = requestBody.getOrDefault("account", "");
        String password = requestBody.getOrDefault("password", "");
        String email = requestBody.getOrDefault("email", "");

        InMemoryUserRepository.save(new User(account, password, email));

        response.setRedirect("/index.html");
    }
}
