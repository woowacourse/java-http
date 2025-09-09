package com.techcourse.controller;

import com.techcourse.service.LoginService;
import java.util.Map;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Map<String, String> queryParams = httpRequest.getQueryParams();

        // account, password 쿼리 파라미터가 둘 다 없는 경우 login.html 반환
        if (!queryParams.containsKey("account")
                && !queryParams.containsKey("password")) {
            String body = ResourceUtil.readStaticResource("/login.html", this.getClass());

            return ResponseEntity.ok(body, "text/html;charset=utf-8");
        }

        // account나 password 중 하나만 없는 경우, 파라미터 누락 처리
        if (!queryParams.containsKey("account")
                || !queryParams.containsKey("password")) {

            return ResponseEntity.badRequest("account or password is missing.");
        }

        String account = queryParams.get("account");
        String password = queryParams.get("password");

        boolean loginSuccess = loginService.login(account, password);
        if (loginSuccess) {
            String body = ResourceUtil.readStaticResource("/index.html", this.getClass());

            return ResponseEntity.found(body, "text/html;charset=utf-8");
        }

        String body = ResourceUtil.readStaticResource("/401.html", this.getClass());

        return ResponseEntity.unauthorized(body, "text/html;charset=utf-8");
    }

    @Override
    public String getPath() {
        return "/login";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
