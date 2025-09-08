package org.apache.coyote.http11;

import com.techcourse.application.LoginService;
import com.techcourse.presentation.Controller;
import com.techcourse.presentation.LoginController;
import com.techcourse.presentation.ParsedResourcePath;
import com.techcourse.presentation.ResponseWithType;
import com.techcourse.presentation.StaticResourceController;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestProcessor {

    private static final List<String> priority = new ArrayList<>();
    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    public RequestProcessor() {
        controllers.computeIfAbsent("StaticResourceController", key -> {
            priority.add(key);
            return new StaticResourceController();
        });

        final var staticResourceController = (StaticResourceController) controllers.get("StaticResourceController");

        controllers.computeIfAbsent(
                "LoginController",
                key -> {
                    priority.add(key);
                    return new LoginController(new LoginService(), staticResourceController);
                }
        );
    }

    public String process(final String requestLine) {
        final ParsedResourcePath request = parse(requestLine);

        final Controller responsibleController = priority.stream()
                .map(controllers::get)
                .filter(controller -> controller.isResponsible(request.path()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청 경로: " + request.path()));

        final ResponseWithType response = responsibleController.getResource(request);

        return createSuccessMessage(response);
    }

    private ParsedResourcePath parse(final String requestLine) {
        if (!requestLine.startsWith("GET")) {
            throw new RuntimeException("현재는 GET 요청만 응답 가능합니다.");
        }

        final String url = requestLine.split(" ")[1];
        final String path = url.split("\\?")[0];
        final Map<String, String> params = new HashMap<>();

        if (url.contains("?")) {
            final String queries = url.split("\\?")[1];

            Arrays.stream(queries.split("&"))
                    .map(query -> Map.entry(query.split("=")[0], query.split("=")[1]))
                    .forEach(entry -> params.put(entry.getKey(), entry.getValue()));
        }

        return new ParsedResourcePath(path, params);
    }

    private String createSuccessMessage(final ResponseWithType response) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + response.contentType() + ";charset=utf-8 ",
                "Content-Length: " + response.body().getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                response.body());
    }
}
