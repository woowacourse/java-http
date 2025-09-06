package com.techcourse.presentation;

import com.techcourse.application.LoginService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String BASE_URL = "/login";

    private final LoginService loginService;

    public LoginController(final LoginService loginService) {
        this.loginService = loginService;
    }

    public static boolean isResponsible(final String path) {
        return path.startsWith(BASE_URL);
    }

    @Override
    public ResponseWithType getResource(final ParsedResourcePath request) {
        if (!BASE_URL.equals(request.path())) {
            log.debug("요청 경로: {}", request.path());
            throw new IllegalArgumentException("요청 경로와 일치하는 API가 존재하지 않습니다.");
        }

        final String filePath = login(request.params());
        return new StaticResourceController().getResource(new ParsedResourcePath(filePath, new HashMap<>()));
    }

    public String login(final Map<String, String> params) {
        if (params.size() != 2 || !params.containsKey("account") || !params.containsKey("password")) {
            log.debug("요청 파라미터: {}", params);
            throw new IllegalArgumentException("적절하지 않은 로그인 요청입니다.");
        }

        loginService.login(params.get("account"), params.get("password"));

        return "/login.html";
    }
}
