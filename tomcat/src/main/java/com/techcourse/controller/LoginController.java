package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

import com.techcourse.model.User;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        // JSESSIONID 쿠키가 있으면 세션에서 user 조회
        String jSessionId = request.getCookie("JSESSIONID");
        if (jSessionId != null) {
            Session session = sessionManager.find(jSessionId);
            if (session != null && session.getAttribute("user") != null) {
                response.setStatusCode(302);
                response.setHeader("Location", "/index.html");
                response.response("");
                return;
            }
        }
        String content = readStaticFile("/login.html");
        response.response(content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        HttpBody httpBody = request.getBody();
        String account = httpBody.getValue("account");
        String password = httpBody.getValue("password");

        if ("gugu".equals(account) && "password".equals(password)) {
            // 로그인 성공 시 세션 생성 및 JSESSIONID 쿠키 추가
            User user = new User(account, password, "");
            String jSessionId = java.util.UUID.randomUUID().toString();
            response.addCookie("JSESSIONID", jSessionId);
            Session session = new Session(jSessionId);
            session.setAttribute("user", user);
            sessionManager.add(session);

            response.setStatusCode(302);
            response.setHeader("Location", "/index.html");
            response.response("");
        } else {
            response.setStatusCode(302);
            response.setHeader("Location", "/401.html");
            response.response("");
        }
    }

    private String readStaticFile(String path) throws IOException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            throw new IllegalArgumentException("File not found: " + path);
        }
        return new String(Files.readAllBytes(Paths.get(resource.toURI())));
    }
}
