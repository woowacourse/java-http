package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLER_MAPPER = new HashMap<>();

    static {
        CONTROLLER_MAPPER.put("/", new HelloController());
        CONTROLLER_MAPPER.put("/login", new LoginController());
        CONTROLLER_MAPPER.put("/register", new RegisterController());
    }

    private RequestMapping() {
    }

    public static Controller mapController(final HttpRequest request) {
        final String uri = request.getPathUri();
        return CONTROLLER_MAPPER.getOrDefault(uri, DefaultController.getInstance());
    }
}
