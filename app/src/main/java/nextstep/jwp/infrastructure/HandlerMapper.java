package nextstep.jwp.infrastructure;

import nextstep.jwp.infrastructure.controller.*;
import nextstep.jwp.model.web.service.LoginService;

import java.util.HashMap;
import java.util.Map;

public class HandlerMapper {

    private final static Map<String, HttpRequestHandler> MAPPER = new HashMap<>();
    private static final String RESOURCE_DELIMITER = ".";

    static {
        MAPPER.put("/", new DefaultController());
        MAPPER.put("/login", new LoginController(loginService()));
        MAPPER.put("/register", new RegisterController());
    }

    public static HttpRequestHandler mappingHandler(String path) {
        if (path.contains(RESOURCE_DELIMITER)) {
            return new ResourceRequestHandler();
        }
        return MAPPER.get(path);
    }

    public static LoginService loginService() {
        return new LoginService();
    }
}
