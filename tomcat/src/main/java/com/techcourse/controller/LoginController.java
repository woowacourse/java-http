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
import java.nio.file.Files;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        if (request.getSession() != null) {
            return HttpResponse.found()
                    .header("Location", "/index.html")
                    .build();
        }
        byte[] body = Files.readAllBytes(getStaticResource("/login.html"));
        return HttpResponse.ok()
                .header("Content-Type", ContentType.TEXT_HTML.getMimeType())
                .header("Content-Length", String.valueOf(body.length))
                .body(body)
                .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(request.getBody("account"));
        if (optionalUser.isEmpty()) {
            log.info("존재하지 않는 유저입니다.");
            return HttpResponse.found()
                    .header("Location", "/401.html")
                    .build();
        }
        User user = optionalUser.get();
        if (!user.checkPassword(request.getBody("password"))) {
            log.info("비밀번호가 일치하지 않습니다.");
            return HttpResponse.found()
                    .header("Location", "/401.html")
                    .build();
        }
        log.info(user.toString());
        if (request.getSession() == null) {
            Session session = createSession(user);
            return HttpResponse.found()
                    .header("Location", "/index.html")
                    .header("Set-Cookie", "JSESSIONID=" + session.getId())
                    .build();
        }
        return HttpResponse.found()
                .header("Location", "/index.html")
                .build();
    }

    private Session createSession(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        return session;
    }
}
