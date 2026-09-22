package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Session session = request.getSession();
        User loginUser = (User) session.getAttribute("user");

        if (loginUser != null) {
            response.redirect("/index.html");
            return;
        }

        URL resource = getClass().getClassLoader().getResource("static/login.html");
        Path filePath = Path.of(resource.toURI());
        String content = Files.readString(filePath, StandardCharsets.UTF_8);

        response.ok("text/html;charset=utf-8", content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> parameters = request.getBody().parseFormData();

        String account = parameters.getOrDefault("account", "");
        String password = parameters.getOrDefault("password", "");

        var user = InMemoryUserRepository.findByAccount(account);
        String location = "/401.html";

        if (user.isPresent() && user.get().checkPassword(password)) {
            request.getSession().setAttribute("user", user.get());
            log.info("로그인 성공 : account={}", user.get().getAccount());
            location = "/index.html";
        }

        response.redirect(location);
    }
}
