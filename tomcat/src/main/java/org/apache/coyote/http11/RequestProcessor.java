package org.apache.coyote.http11;

import com.techcourse.application.LoginService;
import com.techcourse.presentation.Controller;
import com.techcourse.presentation.HttpRequest;
import com.techcourse.presentation.HttpResponse;
import com.techcourse.presentation.LoginController;
import com.techcourse.presentation.RegisterController;
import com.techcourse.presentation.StaticResourceController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.catalina.SessionService;

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
                    return new LoginController(new LoginService(), staticResourceController, new SessionService());
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
        final HttpRequest request = HttpRequestParser.parseHttpRequest(headers, body);

        final Controller responsibleController = priority.stream()
                .map(controllers::get)
                .filter(controller -> controller.isResponsible(request.path()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청 경로: " + request.path()));

        final HttpResponse response = responsibleController.getResource(request);

        return createResponseMessage(response);
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
