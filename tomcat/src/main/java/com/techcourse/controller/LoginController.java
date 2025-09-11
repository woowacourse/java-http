package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.AbstractController;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        if (request.getSession() != null) {
            return HttpResponse.found()
                    .location("/index.html")
                    .build();
        }
        byte[] body = getStaticResource("/login.html");
        return HttpResponse.ok()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(body.length)
                .body(body)
                .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(request.getBody("account"));
        if (optionalUser.isEmpty()) {
            log.info("존재하지 않는 유저입니다.");
            return HttpResponse.found()
                    .location("/401.html")
                    .build();
        }
        User user = optionalUser.get();
        if (!user.checkPassword(request.getBody("password"))) {
            log.info("비밀번호가 일치하지 않습니다.");
            return HttpResponse.found()
                    .location("/401.html")
                    .build();
        }
        log.info(user.toString());
        if (request.getSession() == null) {
            Session session = createSession(user);
            return HttpResponse.found()
                    .location("/index.html")
                    .setCookie("JSESSIONID=" + session.getId())
                    .build();
        }
        return HttpResponse.found()
                .location("/index.html")
                .build();
    }

    private Session createSession(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        return session;
    }
}
