package com.techcourse.controller;

import com.techcourse.service.RegisterService;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeType;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.util.ResourceUtil;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        String body = ResourceUtil.readStaticResource("/register.html", this.getClass());
        return ResponseEntity.ok(body, MimeType.HTML);
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        Map<String, String> bodyParams = httpRequest.getBodyParams();

        if (!isValidParams(bodyParams)) {
            return ResponseEntity.badRequest("account or password is missing.");
        }

        String account = bodyParams.get("account");
        String password = bodyParams.get("password");
        String email = bodyParams.get("email");
        if (registerService.isExistAccount(account)) {
            // account가 중복되어 회원가입에 실패한 경우
            String body = ResourceUtil.readStaticResource("/register.html", this.getClass());
            return ResponseEntity.conflict(body, MimeType.HTML);
        }

        registerService.register(account, password, email);
        // 회원가입에 성공한 경우
        String body = ResourceUtil.readStaticResource("/index.html", this.getClass());
        return ResponseEntity.ok(body, MimeType.HTML);
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
