package nextstep.jwp.mapping;

import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.http.controller.Controller;

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
