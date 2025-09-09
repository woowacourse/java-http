package com.techcourse.controller;

import com.techcourse.service.LoginService;
import java.util.Map;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.util.ResourceUtil;

public class LoginController extends AbstractController {

    private LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
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

        boolean loginSuccess = loginService.login(account, password);
        if (loginSuccess) {
            String body = ResourceUtil.readStaticResource("/index.html", this.getClass());

            httpResponse.setHttpResponse(ResponseEntity.found(body, "text/html;charset=utf-8"));
            return;
        }

        String body = ResourceUtil.readStaticResource("/401.html", this.getClass());
        httpResponse.setHttpResponse(ResponseEntity.unauthorized(body, "text/html;charset=utf-8"));
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
