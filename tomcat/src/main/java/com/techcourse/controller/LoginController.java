package com.techcourse.controller;

import com.techcourse.service.LoginService;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> queryParams = httpRequest.getQueryParams();

        // account, password 쿼리 파라미터가 둘 다 없는 경우 login.html 반환
        if (queryParams.isEmpty()) {
            String body = ResourceUtil.readStaticResource("/login.html", this.getClass());

            httpResponse.setHttpResponse(ResponseEntity.ok(body, "text/html;charset=utf-8"));
            return;
        }

        if (!isValidQueryParams(queryParams)) {
            httpResponse.setHttpResponse(ResponseEntity.badRequest("account or password is missing."));
            return;
        }

        String account = queryParams.get("account");
        String password = queryParams.get("password");

        boolean isLoginSuccess = loginService.login(account, password);
        if (!isLoginSuccess) {
            String body = ResourceUtil.readStaticResource("/401.html", this.getClass());
            httpResponse.setHttpResponse(ResponseEntity.unauthorized(body, "text/html;charset=utf-8"));
            return;
        }

        String body = ResourceUtil.readStaticResource("/index.html", this.getClass());
        httpResponse.setHttpResponse(ResponseEntity.found(body, "text/html;charset=utf-8"));

        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("account", account);
        sessionManager.add(session);
        httpResponse.addCookie("JSESSIONID=" + session.getId());
    }

    private boolean isValidQueryParams(Map<String, String> queryParams) {
        // account나 password 중 하나만 없는 경우, 파라미터 누락 처리
        if (!queryParams.containsKey("account")
                || !queryParams.containsKey("password")) {
            return false;
        }

        return true;
    }

    @Override
    public String getPath() {
        return "/login";
    }
}
