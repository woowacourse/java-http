package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.constants.Header;
import nextstep.jwp.constants.StatusCode;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.request.RequestBody;
import nextstep.jwp.response.ResponseEntity;
import nextstep.jwp.service.HttpService;

public class Controller {

    @GetMapping(path = "/")
    public String basic() {
        return ResponseEntity
                .responseBody("Hello world!")
                .build();
    }

    @GetMapping(path = "/login")
    public String login() throws IOException {
        return ResponseEntity
                .responseResource("/login.html")
                .build();
    }

    @GetMapping(path = "/register")
    public String register() throws IOException {
        return ResponseEntity
                .responseResource("/register.html")
                .build();
    }

    @GetMapping(path = "/index")
    public String index() throws IOException {
        return ResponseEntity
                .responseResource("/index.html")
                .build();
    }

    @PostMapping(path = "/register")
    public String register(RequestBody body) throws IOException {
        Map<String, String> params = body.getParams();
        HttpService.register(params);
        return ResponseEntity
                .statusCode(StatusCode.FOUND)
                .addHeaders(Header.LOCATION, "/index.html")
                .responseResource("/index.html")
                .build();
    }

    @PostMapping(path = "/login")
    public String login(RequestBody body) throws IOException {
        Map<String, String> params = body.getParams();
        if (!HttpService.isAuthorized(params)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
        UUID sessionId = UUID.randomUUID();
        return ResponseEntity
                .statusCode(StatusCode.FOUND)
                .addHeaders(Header.LOCATION, "/index.html")
                .addHeaders(Header.SET_COOKIE, "JSESSIONID=" + sessionId)
                .responseResource("/index.html")
                .build();
    }
}
