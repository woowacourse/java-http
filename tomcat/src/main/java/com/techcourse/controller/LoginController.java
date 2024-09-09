package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.util.StaticResourceManager;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.MediaType;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String STATIC_RESOURCE_PATH = "static/login.html";
    private static final SessionManager sessionManager = new SessionManager();

    public HttpResponse doGet(HttpRequest request) {
        MediaType mediaType = MediaType.fromAcceptHeader(request.getHeaders().get("Accept"));

        // 로그인한 경우 index.html로 리다이렉트
        Optional<HttpSession> session = sessionManager.getSession(request.getSessionId());
        boolean isLogin = session.map(s -> s.getAttribute("user") != null)
                .orElse(false);
        if (isLogin) {
            return HttpResponse.redirect("index.html");
        }

        return new HttpResponse(HttpStatusCode.OK)
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
        HttpSession httpSession = sessionManager.getSession(request.getSessionId())
                .orElse(new Session(UUID.randomUUID().toString()));
        httpSession.setAttribute("user", user);
        sessionManager.add(httpSession);

        return HttpResponse.redirect("index.html").addCookie("JSESSIONID", httpSession.getId());
    }
}
