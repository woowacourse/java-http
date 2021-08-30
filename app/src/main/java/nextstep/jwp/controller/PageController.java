package nextstep.jwp.controller;

import nextstep.jwp.model.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class PageController {
    private final Map<String, BiFunction<HttpStatus, String, Map<HttpStatus, String>>> mappedFunction;

    public PageController() {
        this.mappedFunction = new HashMap<>();
        this.mappedFunction.put("index", this::index);
        this.mappedFunction.put("index.html", this::index);
        this.mappedFunction.put("login", this::login);
        this.mappedFunction.put("401", this::unauthorized);
        this.mappedFunction.put("register", this::register);
    }

    public Map<HttpStatus, String> mapResponse(final Optional<HttpStatus> status, final String request) {
        return this.mappedFunction.keySet().stream()
                .filter(request::contains)
                .map(s -> this.mappedFunction.get(s).apply(status.orElse(HttpStatus.OK), request))
                .findAny()
                .orElse(Map.of(HttpStatus.NOT_FOUND, "해당 페이지가 존재하지 않습니다."));
    }

    public Map<HttpStatus, String> index(final HttpStatus status, final String indexRequest) {
        return makeResponseWithStatus(status, "static/index.html");
    }

    public Map<HttpStatus, String> login(final HttpStatus status, final String requestPage) {
        return makeResponseWithStatus(status, "static/login.html");
    }

    public Map<HttpStatus, String> unauthorized(final HttpStatus status, final String requestPage) {
        return makeResponseWithStatus(status, "static/401.html");
    }

    public Map<HttpStatus, String> register(final HttpStatus status, final String requestPage) {
        return makeResponseWithStatus(status, "static/register.html");
    }

    private Map<HttpStatus, String> makeResponseWithStatus(HttpStatus status, String s) {
        if (status != HttpStatus.OK) {
            return makeResponse(status, s);
        }
        return makeResponse(HttpStatus.OK, s);
    }

    private Map<HttpStatus, String> makeResponse(final HttpStatus status, final String request) {
        try {
            final String fileResponse = getFileResponse(request);
            return Map.of(status, fileResponse);
        } catch (RuntimeException e) {
            return Map.of(status, "해당 페이지가 존재하지 않습니다.");
        }
    }

    public String getFileResponse(final String request) {
        try {
            final Path path = new File(getPath(request)).toPath();
            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("옳지 않은 url입니다.");
        }
    }

    private String getPath(final String request) {
        final URL url = getClass().getClassLoader().getResource(request);
        if (url == null) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }
        return url.getPath();
    }
}
