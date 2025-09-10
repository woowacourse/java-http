package com.techcourse.presentation;

import com.techcourse.application.LoginService;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String BASE_URL = "/login";

    private final LoginService loginService;
    private final StaticResourceController staticResourceController;
    private final SessionService sessionService;

    public LoginController(
            final LoginService loginService,
            final StaticResourceController staticResourceController,
            final SessionService sessionService
    ) {
        this.loginService = loginService;
        this.staticResourceController = staticResourceController;
        this.sessionService = sessionService;
    }

    public HttpResponse renderLoginPage(final HttpRequest request) {
        if (sessionService.isValidSession(request)) {
            return createRedirectResponseBuilder("http://localhost:8080/index.html", request.protocol()).build();
        }

        return staticResourceController.getResource(createStaticRequest("/login.html", request.protocol()));
    }

    public HttpResponse login(final HttpRequest request) {
        final Map<String, String> params = request.params();

        final User user;
        try {
            if (params.size() != 2 || !params.containsKey("account") || !params.containsKey("password")) {
                log.debug("요청 파라미터: {}", params);
                throw new IllegalArgumentException("적절하지 않은 로그인 요청입니다.");
            }

            user = loginService.login(params.get("account"), params.get("password"));
        } catch (IllegalArgumentException e) {
            return staticResourceController.getResource(createStaticRequest("/401.html", request.protocol()));
        }

        final HttpResponse.Builder responseBuilder = createRedirectResponseBuilder(
                "http://localhost:8080/index.html",
                request.protocol()
        );

        final Optional<String> sessionCookie = sessionService.createSessionCookie(user, request);
        sessionCookie.ifPresent(cookie -> responseBuilder.header("Set-Cookie", cookie));

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
            return renderLoginPage(request);
        }
        if ("POST".equals(request.method())) {
            return login(request);
        }
        throw new IllegalArgumentException("요청 경로에 일치하는 메서드가 없습니다.");
    }

    private HttpRequest createStaticRequest(final String path, final String protocol) {
        return new HttpRequest("GET", path, protocol, new HashMap<>(), new HashMap<>());
    }

    private HttpResponse.Builder createRedirectResponseBuilder(final String location, final String protocol) {
        return HttpResponse.builder()
                .protocol(protocol)
                .found()
                .header("Location", location)
                .contentType("text/html;charset=utf-8")
                .contentLength(0);
    }
}
