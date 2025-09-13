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

    public String process(final HttpRequest request) {
        final Controller responsibleController = priority.stream()
                .map(controllers::get)
                .filter(controller -> controller.isResponsible(request.requestLine().getUri()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청 경로: " + request.requestLine().getUri()));

        final HttpResponse response = responsibleController.getResource(request);

        return response.toMessage();
    }
}
