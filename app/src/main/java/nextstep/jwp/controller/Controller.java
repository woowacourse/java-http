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
    public String basic(String uri) {
        return ResponseEntity
                .responseBody("Hello world!")
                .build();
    }

    @GetMapping(path = "/login")
    public String login(String uri) throws IOException {
        return ResponseEntity
                .responseResource(uri)
                .build();
    }

    @GetMapping(path = "/register")
    public String register(String uri) throws IOException {
        return ResponseEntity
                .responseResource(uri)
                .build();
    }

    @GetMapping(path = "/index")
    public String index(String uri) throws IOException {
        return ResponseEntity
                .responseResource(uri)
                .build();
    }

    @PostMapping(path = "/register")
    public String register(String uri, RequestBody body) throws IOException {
        Map<String, String> params = body.getParams();
        service.register(params);
        return ResponseEntity
                .statusCode(StatusCode.FOUND)
                .responseResource("/index.html")
                .build();
    }

    @PostMapping(path = "/login")
    public String login(String uri, RequestBody body) throws IOException {
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
