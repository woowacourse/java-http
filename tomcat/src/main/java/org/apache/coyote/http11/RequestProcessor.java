package org.apache.coyote.http11;

import com.techcourse.application.LoginService;
import com.techcourse.presentation.Controller;
import com.techcourse.presentation.HttpRequest;
import com.techcourse.presentation.HttpResponse;
import com.techcourse.presentation.LoginController;
import com.techcourse.presentation.RegisterController;
import com.techcourse.presentation.StaticResourceController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
        controllers.computeIfAbsent(
                "RegisterController",
                key -> {
                    priority.add(key);
                    return new RegisterController(new LoginService(), staticResourceController);
                }
        );
    }

    public String process(final List<String> headers, final String body) {
        final HttpRequest request = parse(headers, body);

        final Controller responsibleController = priority.stream()
                .map(controllers::get)
                .filter(controller -> controller.isResponsible(request.path()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청 경로: " + request.path()));

        final HttpResponse response = responsibleController.getResource(request);

        return createResponseMessage(response);
    }

    private HttpRequest parse(final List<String> headers, final String body) {
        final String requestLine = headers.getFirst();
        final String[] parts = requestLine.split(" ");

        final String method = parts[0];
        final String url = parts[1];
        final String path = url.split("\\?")[0];
        final String protocol = parts[2].trim();
        final Map<String, String> requestParams = new HashMap<>();

        if (url.contains("?")) {
            final String queries = url.split("\\?")[1];

            Arrays.stream(queries.split("&"))
                    .map(query -> Map.entry(query.split("=")[0], query.split("=")[1]))
                    .forEach(entry -> requestParams.put(entry.getKey(), entry.getValue()));
        }

        final Map<String, String> requestHeaders = new HashMap<>();
        for (int i = 1; i < headers.size(); ++i) {
            final String header = headers.get(i);
            requestHeaders.put(header.split(":")[0].trim(), header.split(":")[1].trim());
        }

        return new HttpRequest(method, path, protocol, requestParams, requestHeaders, body);
    }

    private String createResponseMessage(final HttpResponse response) {
        final Map<String, String> headers = response.headers();
        final String header = headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                response.protocol() + " " + response.statusCode() + " ",
                header,
                "",
                response.body());
    }
}
