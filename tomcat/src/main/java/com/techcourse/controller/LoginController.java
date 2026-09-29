package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        //이미 로그인한 상태면 로그인 페이지를 보여줄 필요가 없음
        if (isLoggedIn(request.getCookie())) {
            response.sendRedirect("/index.html");
            return;
        }
        response.setBody(new StaticResource("/login.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> params = request.getParams();

        Optional<User> user = InMemoryUserRepository.findByAccount(params.getOrDefault("account", ""))
                .filter(it -> it.checkPassword(params.get("password")));

        if (user.isEmpty()) {
            response.setStatusLine(new StatusLine(401, "Unauthorized"));
            response.setBody(new StaticResource("/401.html"));
            return;
        }

        log.info("user : {}", user.get());

        //로그인 정보는 서버(세션)에 두고, 클라이언트에는 세션 아이디만 내려보냄
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user.get());
        SessionManager.getInstance().add(session);

        response.sendRedirect("/index.html");
        response.addHeader("Set-Cookie", Cookie.ofJSessionId(session.getId()));
    }

    private boolean isLoggedIn(Cookie cookie) {
        if (!cookie.hasJSessionId()) {
            return false;
        }

        Session session = SessionManager.getInstance().findSession(cookie.getJSessionId());
        if (session == null) {
            return false;
        }

        return session.getAttribute("user") != null;
    }
}
