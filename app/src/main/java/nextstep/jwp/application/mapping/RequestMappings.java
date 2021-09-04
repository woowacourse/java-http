package nextstep.jwp.application.mapping;

import nextstep.jwp.application.controller.HelloController;
import nextstep.jwp.application.controller.LoginController;
import nextstep.jwp.application.controller.RegisterController;
import nextstep.jwp.framework.controller.Controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMappings {

    private static final RequestMappings instance = new RequestMappings();

    private final Map<String, Controller> mappings = new HashMap<>();

    {
        mappings.put("/", new HelloController());
        mappings.put("/login", new LoginController());
        mappings.put("/register", new RegisterController());
    }

    private RequestMappings() {
    }

    public static RequestMappings getInstance() {
        return instance;
    }

    public Map<String, Controller> getMappings() {
        return mappings;
    }
}
