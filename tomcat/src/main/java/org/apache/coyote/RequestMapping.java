package org.apache.coyote;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestMapping {

    private static final String LOGIN_PATH = "/login";

    private static final String REGISTER_PATH = "/register";

    private final Map<String, Controller> controllers = new HashMap<>();

    public RequestMapping(final SessionManager sessionManager) {
        controllers.put(LOGIN_PATH, new LoginController(sessionManager));
        controllers.put(REGISTER_PATH, new RegisterController());
    }

    public Optional<Controller> getController(final HttpRequest request) {
        return Optional.ofNullable(controllers.get(request.getPath()));
    }
}