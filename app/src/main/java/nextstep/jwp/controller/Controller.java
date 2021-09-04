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
            return redirectTo(header, "/index.html");
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
        RequestHeader header = request.getRequestHeader();
        RequestBody body = request.getRequestBody();
        Map<String, String> params = body.getParams();
        HttpService.register(params);
        return redirectTo(header, "/index.html");
    }

    @PostMapping(path = "/login")
    public String postLogin(HttpRequest request) throws IOException {
        RequestBody body = request.getRequestBody();
        RequestHeader header = request.getRequestHeader();
        Map<String, String> params = body.getParams();
        if (header.contains(HeaderType.COOKIE.getValue())) {
            return redirectTo(header, "/index.html");
        }
        if (!HttpService.isAuthorized(params)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
        final UUID sessionId = UUID.randomUUID();
        setSession(params, sessionId);
        return HttpResponse
                .statusCode(StatusCode.FOUND)
                .addHeaders(HeaderType.LOCATION, "/index.html")
                .addHeaders(HeaderType.SET_COOKIE, HttpTerms.JSESSIONID + HttpTerms.EQUAL_SEPARATOR + sessionId)
                .responseResource("/index.html")
                .build();
    }

    private void setSession(Map<String, String> params, UUID sessionId) {
        HttpSessions.addSession(sessionId.toString());
        HttpSession session = HttpSessions.getSession(sessionId.toString());
        User user = HttpService.findUser(params.get(UserParams.ACCOUNT));
        session.setAttribute("user", user);
    }

    private void checkCookieSessionId(RequestHeader header) {
        HttpCookie cookie = header.getCookie();
        if (!cookie.contains(HttpTerms.JSESSIONID)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
        String sessionId = cookie.get(HttpTerms.JSESSIONID);
        HttpSession session = HttpSessions.getSession(sessionId);
        Object user = session.getAttribute("user");
        if (Objects.isNull(user)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
    }

    private String redirectTo(RequestHeader header, String uri) throws IOException {
        checkCookieSessionId(header);
        return HttpResponse
                .statusCode(StatusCode.FOUND)
                .addHeaders(HeaderType.LOCATION, uri)
                .responseResource(uri)
                .build();
    }
}
