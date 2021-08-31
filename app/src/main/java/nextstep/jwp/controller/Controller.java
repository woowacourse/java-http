package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.http.RequestBody;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.http.StatusCode;
import nextstep.jwp.service.HttpService;

public class Controller {

    private final HttpService service;

    public Controller() {
        this.service = new HttpService();
    }

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
        service.register(params);
        return ResponseEntity
                .statusCode(StatusCode.FOUND)
                .responseResource("/index.html")
                .build();
    }

    @PostMapping(path = "/login")
    public String login(RequestBody body) throws IOException {
        Map<String, String> params = body.getParams();
        if (service.isAuthorized(params)) {
            return ResponseEntity
                    .statusCode(StatusCode.FOUND)
                    .responseResource("/index.html")
                    .build();
        }
        return ResponseEntity
                .statusCode(StatusCode.UNAUTHORIZED)
                .responseResource("/401.html")
                .build();

    }
}
