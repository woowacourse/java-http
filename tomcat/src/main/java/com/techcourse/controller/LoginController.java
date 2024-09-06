package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.util.StaticResourceManager;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String STATIC_RESOURCE_PATH = "static/login.html";
    private static final SessionManager sessionManager = new SessionManager();

    public HttpResponse doGet(HttpRequest request) {
        MediaType mediaType = MediaType.fromAcceptHeader(request.getAccept());

        // 로그인한 경우 index.html로 리다이렉트
        Optional<HttpSession> session = request.getSession(sessionManager);
        boolean isLogin = session.map(s -> s.getAttribute("user") != null)
                .orElse(false);
        if (isLogin) {
            return HttpResponse.redirect("index.html");
        }

        return new HttpResponse(200, "OK")
                .addHeader("Content-Type", mediaType.getValue())
                .setBody(StaticResourceManager.read(STATIC_RESOURCE_PATH));
    }

    public HttpResponse doPost(HttpRequest request) {
        log.info("Query Parameters: {}", request.parseFormBody());

        Optional<String> userAccount = request.getFormValue("account");
        Optional<String> userPassword = request.getFormValue("password");

        User user = userAccount.map(InMemoryUserRepository::findByAccount)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(null);

        if (user == null || !user.checkPassword(userPassword.get())) {
            return HttpResponse.redirect("401.html");
        }

        // 세션이 있으면 세션에 유저 정보를 넣어주고, 없다면 생성해서 넣어줌
        HttpSession httpSession = request.getSession(sessionManager)
                .orElse(new Session(UUID.randomUUID().toString()));
        httpSession.setAttribute("user", user);
        sessionManager.add(httpSession);

        return HttpResponse.redirect("index.html").addCookie("JSESSIONID", httpSession.getId());
    }
}
