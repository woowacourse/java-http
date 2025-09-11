package com.techcourse.controller;

import com.techcourse.service.LoginService;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Cookie;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeType;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.util.ResourceUtil;

public class LoginController extends AbstractController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    public LoginController(LoginService loginService, SessionManager sessionManager) {
        this.loginService = loginService;
        this.sessionManager = sessionManager;
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        if (isLoginUser(httpRequest)) {
            String body = ResourceUtil.readStaticResource("/index.html", this.getClass());
            return ResponseEntity.found(body, MimeType.HTML, "/index.html");
        }

        String body = ResourceUtil.readStaticResource("/login.html", this.getClass());
        return ResponseEntity.ok(body, MimeType.HTML);
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        Map<String, String> bodyParams = httpRequest.getBodyParams();

        // account, password 쿼리 파라미터가 둘 다 없는 경우 login.html 반환
        if (bodyParams.isEmpty()) {
            String body = ResourceUtil.readStaticResource("/login.html", this.getClass());

            return ResponseEntity.ok(body, MimeType.HTML);
        }

        if (!isValidParams(bodyParams)) {
            return ResponseEntity.badRequest("account or password is missing.");
        }

        String account = bodyParams.get("account");
        String password = bodyParams.get("password");

        boolean isLoginSuccess = loginService.login(account, password);
        if (!isLoginSuccess) {
            String body = ResourceUtil.readStaticResource("/401.html", this.getClass());
            return ResponseEntity.unauthorized(body, MimeType.HTML);
        }

        String body = ResourceUtil.readStaticResource("/index.html", this.getClass());
        HttpResponse response = ResponseEntity.found(body, MimeType.HTML, "/index.html");

        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(session.getId(), account);
        sessionManager.add(session);
        response.addCookie("JSESSIONID=" + session.getId());
        return response;
    }

    private boolean isLoginUser(HttpRequest httpRequest) {
        Cookie cookie = httpRequest.getCookie();
        String sessionIdInCookie = cookie.getAttribute("JSESSIONID");
        if (sessionIdInCookie == null) {
            return false;
        }

        Session session = sessionManager.findSession(sessionIdInCookie);
        if (session == null) {
            return false;
        }

        return sessionIdInCookie.equals(session.getId());
    }

    private boolean isValidParams(Map<String, String> params) {
        // account나 password 중 하나만 없는 경우, 파라미터 누락 처리
        if (!params.containsKey("account")
                || !params.containsKey("password")) {
            return false;
        }

        return true;
    }

    @Override
    public String getPath() {
        return "/login";
    }
}
