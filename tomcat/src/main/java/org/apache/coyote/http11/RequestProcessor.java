package org.apache.coyote.http11;

import com.techcourse.application.LoginService;
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
        controllers.computeIfAbsent("LoginController", key -> new LoginController(new LoginService()));
        controllers.computeIfAbsent("RegisterController", key -> new RegisterController(new LoginService()));
        controllers.computeIfAbsent("StaticResourceController", key -> new StaticResourceController());
    }

    public String process(final HttpRequest request) {
        final String uri = request.requestLine().getUri();
        final Controller controller = getController(uri);
        final HttpResponse response = controller.service(request);
        return response.toMessage();
    }

    private Controller getController(final String uri) {
        if ("/login".equals(uri)) {
            return controllers.get("LoginController");
        }
        if ("/register".equals(uri)) {
            return controllers.get("RegisterController");
        }
        return controllers.get("StaticResourceController");
    }
}
