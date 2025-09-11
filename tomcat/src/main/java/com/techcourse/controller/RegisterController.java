package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.AbstractController;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        byte[] body = getStaticResource("/register.html");
        return HttpResponse.ok()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(body.length)
                .body(body)
                .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        User user = new User(request.getBody("account"), request.getBody("password"), request.getBody("email"));
        InMemoryUserRepository.save(user);
        Session session = createSession(user);
        return HttpResponse.found()
                .location("/index.html")
                .setCookie("JSESSIONID=" + session.getId())
                .build();
    }

    private Session createSession(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        return session;
    }
}
