package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.constants.Header;
import nextstep.jwp.constants.Http;
import nextstep.jwp.constants.StatusCode;
import nextstep.jwp.constants.UserParams;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpCookie;
import nextstep.jwp.request.HttpSession;
import nextstep.jwp.request.HttpSessions;
import nextstep.jwp.request.RequestBody;
import nextstep.jwp.request.RequestHeader;
import nextstep.jwp.response.ResponseEntity;
import nextstep.jwp.service.HttpService;

public class Controller {

    @GetMapping(path = "/")
    public String basic(RequestHeader header) {
        return ResponseEntity
                .responseBody("Hello world!")
                .build();
    }

    @GetMapping(path = "/login")
    public String login(RequestHeader header) throws IOException {
        if (header.contains(Header.COOKIE.getKey())) {
            return redirectToIndex(header);
        }
        return ResponseEntity
                .responseResource("/login.html")
                .build();
    }

    @GetMapping(path = "/register")
    public String register(RequestHeader header) throws IOException {
        return ResponseEntity
                .responseResource("/register.html")
                .build();
    }

    @GetMapping(path = "/index")
    public String index(RequestHeader header) throws IOException {
        return ResponseEntity
                .responseResource("/index.html")
                .build();
    }

    @PostMapping(path = "/register")
    public String register(RequestHeader header, RequestBody body) throws IOException {
        Map<String, String> params = body.getParams();
        HttpService.register(params);
        return ResponseEntity
                .statusCode(StatusCode.FOUND)
                .addHeaders(Header.LOCATION, "/index.html")
                .responseResource("/index.html")
                .build();
    }

    @PostMapping(path = "/login")
    public String login(RequestHeader header, RequestBody body) throws IOException {
        Map<String, String> params = body.getParams();
        if (header.contains(Header.COOKIE.getKey())) {
            return redirectToIndex(header);
        }
        if (!HttpService.isAuthorized(params)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
        final UUID sessionId = UUID.randomUUID();
        setSession(params, sessionId);
        return ResponseEntity
                .statusCode(StatusCode.FOUND)
                .addHeaders(Header.LOCATION, "/index.html")
                .addHeaders(Header.SET_COOKIE, Http.JSESSIONID + Http.EQUAL_SEPARATOR + sessionId)
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
        if (!cookie.contains(Http.JSESSIONID)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
        String sessionId = cookie.get(Http.JSESSIONID);
        HttpSession session = HttpSessions.getSession(sessionId);
        Object user = session.getAttribute("user");
        if (Objects.isNull(user)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
    }

    private String redirectToIndex(RequestHeader header) throws IOException {
        checkCookieSessionId(header);
        return ResponseEntity
                .statusCode(StatusCode.FOUND)
                .addHeaders(Header.LOCATION, "/index.html")
                .responseResource("/index.html")
                .build();
    }
}
