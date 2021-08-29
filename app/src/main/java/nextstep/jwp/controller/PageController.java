package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PageController {
    private final Map<String, Function<String, Map<Integer, String>>> mappedFunction;

    public PageController() {
        this.mappedFunction = new HashMap<>();
        this.mappedFunction.put("index", this::index);
        this.mappedFunction.put("index.html", this::index);
        this.mappedFunction.put("login", this::login);
    }

    public Map<Integer, String> index(final String indexRequest) {
        if (!"".equals(indexRequest) && !indexRequest.contains("index")) {
            throw new IllegalArgumentException("옳지 않은 페이지명입니다.");
        }
        return makeResponse("static/index.html");
    }

    public Map<Integer, String> login(final String requestPage) {
        if (!requestPage.contains("login")) {
            throw new IllegalArgumentException("옳지 않은 페이지명입니다.");
        }
        return makeResponse("static/login.html");
    }

    private Map<Integer, String> makeResponse(String request) {
        try {
            final URL url = getClass().getClassLoader().getResource(request);
            final Path path = new File(url.getPath()).toPath();
            return Map.of(200, Files.readString(path));
        } catch (IOException e) {
            return Map.of(404, "해당 페이지가 존재하지 않습니다.");
        }
    }

    public Map<Integer, String> mapResponse(final String request) {
        return this.mappedFunction.keySet().stream()
                .filter(request::contains)
                .map(s -> this.mappedFunction.get(s).apply(request))
                .findAny()
                .orElse(Map.of(404, "해당 페이지가 존재하지 않습니다."));
    }
}
