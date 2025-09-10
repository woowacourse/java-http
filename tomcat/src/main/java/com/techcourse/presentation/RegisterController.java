package com.techcourse.presentation;

import com.techcourse.application.LoginService;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
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
                new HttpRequest("GET", BASE_URL + ".html", protocol, new HashMap<>(), new HashMap<>())
        );
    }

    public HttpResponse register(final HttpRequest request) {
        final Map<String, String> params = request.params();
        final String account = params.get("account");
        final String password = params.get("password");
        final String email = params.get("email");

        try {
            if (account == null || password == null || email == null ||
                account.isBlank() || password.isBlank() || email.isBlank()) {

                log.debug("요청 파라미터: {}", params);
                throw new IllegalArgumentException("적절하지 않은 회원가입 요청입니다.");
            }

            final User user = loginService.register(params.get("account"), params.get("password"), params.get("email"));

            if (user != null) {
                final Session session = request.getSession(true);
                session.setAttribute(user.getAccount(), user);
                SessionManager.getInstance().add(session);

                final String body = "회원가입이 완료되었습니다.";
                return HttpResponse.builder()
                        .protocol(request.protocol())
                        .seeOther()
                        .header("Location", "http://localhost:8080/index.html")
                        .addCookie("JSESSIONID" + "=" + session.getId())
                        .contentType("text/html;charset=utf-8")
                        .contentLength(body.getBytes(StandardCharsets.UTF_8).length)
                        .body(body)
                        .build();
            }
        } catch (IllegalArgumentException e) {
            final String errorMessage = e.getMessage();

            return HttpResponse.builder()
                    .protocol(request.protocol())
                    .badRequest()
                    .contentType("text/plain;charset=utf-8")
                    .contentLength(errorMessage.getBytes(StandardCharsets.UTF_8).length)
                    .body(errorMessage)
                    .build();
        }

        return register(request.protocol());
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
