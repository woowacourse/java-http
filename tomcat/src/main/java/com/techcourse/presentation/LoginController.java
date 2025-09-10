package com.techcourse.presentation;

import com.techcourse.application.LoginService;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.MyCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String BASE_URL = "/login";

    private final LoginService loginService;
    private final StaticResourceController staticResourceController;

    public LoginController(final LoginService loginService, final StaticResourceController staticResourceController) {
        this.loginService = loginService;
        this.staticResourceController = staticResourceController;
    }

    public HttpResponse login(final String protocol) {
        return staticResourceController.getResource(
                new HttpRequest("GET", "/login.html", protocol, new HashMap<>(), new HashMap<>())
        );
    }

    public HttpResponse login(final HttpRequest request) {
        final Map<String, String> params = request.params();

        try {
            if (params.size() != 2 || !params.containsKey("account") || !params.containsKey("password")) {
                log.debug("요청 파라미터: {}", params);
                throw new IllegalArgumentException("적절하지 않은 로그인 요청입니다.");
            }

            final User user = loginService.login(params.get("account"), params.get("password"));
        } catch (IllegalArgumentException e) {
            return staticResourceController.getResource(
                    new HttpRequest("GET", "/401.html", request.protocol(), new HashMap<>(), new HashMap<>())
            );
        }

        final HttpResponse.Builder responseBuilder = HttpResponse.builder()
                .protocol(request.protocol())
                .found()
                .header("Location", "http://localhost:8080/index.html")
                .contentType("text/html;charset=utf-8")
                .contentLength(0);

        HttpCookie httpCookie = new HttpCookie(request);
        if (!httpCookie.hasAttribute("JSESSIONID")) {
            final UUID token = UUID.randomUUID();
            final MyCookie cookie = new MyCookie("JSESSIONID", token.toString());
            responseBuilder.header("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
        }

        return responseBuilder.build();
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
            return login(request.protocol());
        }
        if ("POST".equals(request.method())) {
            return login(request);
        }
        throw new IllegalArgumentException("요청 경로에 일치하는 메서드가 없습니다.");
    }
}
