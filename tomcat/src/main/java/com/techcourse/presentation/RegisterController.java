package com.techcourse.presentation;

import com.techcourse.application.LoginService;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String BASE_URL = "/register";

    private final LoginService loginService;
    private final StaticResourceController staticResourceController;

    public RegisterController(
            final LoginService loginService,
            final StaticResourceController staticResourceController
    ) {
        this.loginService = loginService;
        this.staticResourceController = staticResourceController;
    }

    public HttpResponse register(final String protocol) {
        return staticResourceController.getResource(
                new HttpRequest("GET", BASE_URL + ".html", protocol, new HashMap<>(), new HashMap<>(), "")
        );
    }

    public HttpResponse register(final HttpRequest request) {
        final String body = request.body();
        final Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            final int index = pair.indexOf('=');
            final String k = URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8);
            final String v = URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8);
            params.put(k, v);
        }

        try {
            if (params.size() != 3 || !params.containsKey("account") || !params.containsKey("password")
                || !params.containsKey("email")) {
                log.debug("요청 파라미터: {}", params);
                throw new IllegalArgumentException("적절하지 않은 회원가입 요청입니다.");
            }

            loginService.register(params.get("account"), params.get("password"), params.get("email"));
        } catch (IllegalArgumentException e) {
            final String errorMessage = e.getMessage();

            final Map<String, String> responseHeaders = new LinkedHashMap<>();
            responseHeaders.put("Content-Type", "text/plain;charset=utf-8");
            responseHeaders.put("Content-Length", String.valueOf(errorMessage.getBytes(StandardCharsets.UTF_8).length));

            return new HttpResponse(request.protocol(), "400 Bad Request", responseHeaders, errorMessage);
        }

        final String redirectPath = "http://localhost:8080/index.html";

        final Map<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Location", redirectPath);
        responseHeaders.put("Content-Type", "text/html;charset=utf-8");
        responseHeaders.put("Content-Length", "0");

        return new HttpResponse(request.protocol(), "303 See Other", responseHeaders, "회원가입이 완료되었습니다.");
    }

    @Override
    public boolean isResponsible(final String path) {
        return path.startsWith(BASE_URL);
    }

    @Override
    public HttpResponse getResource(final HttpRequest request) {
        if (!BASE_URL.equals(request.path())) {
            log.debug("요청 경로: {}", request.path());
            throw new IllegalArgumentException("요청 경로와 일치하는 API가 존재하지 않습니다.");
        }

        if ("GET".equals(request.method())) {
            return register(request.protocol());
        }
        if ("POST".equals(request.method())) {
            return register(request);
        }
        throw new IllegalArgumentException("요청 경로에 일치하는 메서드가 없습니다.");
    }
}
