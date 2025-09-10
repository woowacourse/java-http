package com.techcourse.controller;

import com.techcourse.service.RegisterService;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.util.ResourceUtil;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = ResourceUtil.readStaticResource("/register.html", this.getClass());
        httpResponse.setHttpResponse(ResponseEntity.ok(body, "text/html;charset=utf-8"));
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> bodyParams = httpRequest.getBodyParams();

        if (!isValidParams(bodyParams)) {
            httpResponse.setHttpResponse(ResponseEntity.badRequest("account or password is missing."));
            return;
        }

        String account = bodyParams.get("account");
        String password = bodyParams.get("password");
        String email = bodyParams.get("email");
        if (registerService.isExistAccount(account)) {
            // account가 중복되어 회원가입에 실패한 경우
            String body = ResourceUtil.readStaticResource("/register.html", this.getClass());
            httpResponse.setHttpResponse(ResponseEntity.conflict(body, "text/html;charset=utf-8"));
            return;
        }

        registerService.register(account, password, email);
        // 회원가입에 성공한 경우
        String body = ResourceUtil.readStaticResource("/index.html", this.getClass());
        httpResponse.setHttpResponse(ResponseEntity.ok(body, "text/html;charset=utf-8"));
    }

    private boolean isValidParams(Map<String, String> params) {
        if (!params.containsKey("account")
                || !params.containsKey("password")
                || !params.containsKey("email")) {
            return false;
        }

        return true;
    }

    @Override
    public String getPath() {
        return "/register";
    }
}
