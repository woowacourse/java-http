package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public boolean support(HttpRequest request) {
        return request.getRequestUrl()
                .startsWith("/login");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Session session = request.getSession(false);
        if (session != null && session.getAttribute("loginUser") != null) {
            response.redirect("index.html");
            return;
        }

        URL resource = getClass().getClassLoader()
                .getResource("static/login.html");
        Path resourcePath = Path.of(resource.getPath());

        response.ok()
                .writeStaticResource(resourcePath);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(request.getParameter("account"));
        if (foundUser.isEmpty()) {
            log.info("존재하지 않는 user입니다.");
            response.redirect("401.html");
            return;
        }

        User user = foundUser.get();
        if (!user.checkPassword(request.getParameter("password"))) {
            log.info("비밀번호 틀림");
            response.redirect("401.html");
            return;
        }

        log.info("user = {}", user);
        Session session = request.getSession(true);
        session.setAttribute("loginUser", user);

        response.redirect("index.html")
                .addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
    }
}
