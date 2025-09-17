package org.apache.coyote.http11;

import com.techcourse.application.UserService;
import com.techcourse.presentation.Controller;
import com.techcourse.presentation.HttpRequest;
import com.techcourse.presentation.HttpResponse;
import com.techcourse.presentation.LoginController;
import com.techcourse.presentation.RegisterController;
import com.techcourse.presentation.StaticResourceController;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestProcessor {

    private final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    public RequestProcessor() {
        controllers.computeIfAbsent("LoginController", key -> new LoginController(new UserService()));
        controllers.computeIfAbsent("RegisterController", key -> new RegisterController(new UserService()));
        controllers.computeIfAbsent("StaticResourceController", key -> new StaticResourceController());
    }

    public String process(final HttpRequest request) {
        final String uri = request.getUri();
        final Controller controller = getController(uri);
        final HttpResponse response = controller.service(request);
        return response.toMessage();
    }

    private Controller getController(final String uri) {
        return controllers.values().stream()
                .filter(controller -> controller.canHandle(uri))
                .findFirst()
                .orElse(controllers.get("StaticResourceController"));
    }
}
