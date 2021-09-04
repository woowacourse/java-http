package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.constants.HeaderType;
import nextstep.jwp.constants.HttpTerms;
import nextstep.jwp.constants.StatusCode;
import nextstep.jwp.constants.UserParams;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpCookie;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.HttpSession;
import nextstep.jwp.request.HttpSessions;
import nextstep.jwp.request.RequestBody;
import nextstep.jwp.request.RequestHeader;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.service.HttpService;

public class Controller {

    @GetMapping(path = "/")
    public String basic(HttpRequest request) {
        return HttpResponse
                .responseBody("Hello world!")
                .build();
    }

    @GetMapping(path = "/login")
    public String login(HttpRequest request) throws IOException {
        RequestHeader header = request.getRequestHeader();
        if (header.contains(HeaderType.COOKIE.getValue())) {
            checkValidSessionId(header.getCookie());
            return HttpResponse
                    .redirectTo("/index.html");
        }
        return HttpResponse
                .responseResource("/login.html")
                .build();
    }

    @GetMapping(path = "/register")
    public String register(HttpRequest request) throws IOException {
        return HttpResponse
                .responseResource("/register.html")
                .build();
    }

    @GetMapping(path = "/index")
    public String index(HttpRequest request) throws IOException {
        return HttpResponse
                .responseResource("/index.html")
                .build();
    }

    @PostMapping(path = "/register")
    public String postRegister(HttpRequest request) throws IOException {
        RequestBody body = request.getRequestBody();
        Map<String, String> params = body.getParams();
        HttpService.register(params);
        return HttpResponse
                .redirectTo("/index.html");
    }

    public Controller() {
    }

    @PostMapping(path = "/login")
    public String postLogin(HttpRequest request) throws IOException {
        RequestBody body = request.getRequestBody();
        Map<String, String> params = body.getParams();
        validateAuthorization(params);
        final String sessionId = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(sessionId);
        HttpSessions.addSession(httpSession);
        User user = HttpService.findUser(params.get(UserParams.ACCOUNT));
        httpSession.setAttribute("user", user);
        return HttpResponse
                .statusCode(StatusCode.FOUND)
                .addHeaders(HeaderType.LOCATION, "/index.html")
                .addHeaders(HeaderType.SET_COOKIE, HttpCookie.toSetCookieValue(sessionId))
                .responseResource("/index.html")
                .build();
    }

    private void validateAuthorization(Map<String, String> params) {
        if (!HttpService.isAuthorized(params)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
    }

    private void checkValidSessionId(HttpCookie cookie) {
        if (!cookie.contains(HttpTerms.JSESSIONID)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
        String sessionId = cookie.get(HttpTerms.JSESSIONID);
        Object user;
        try {
            HttpSession session = HttpSessions.getSession(sessionId);
            user = session.getAttribute("user");
        } catch (NullPointerException e) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
        if (Objects.isNull(user)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
    }
}
