package nextstep.jwp.web;

import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.request.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestMappingConfig {

    private RequestMappingConfig() {
    }

    public static Map<RequestMappingInfo, ControllerMethod> config() {
        final Map<RequestMappingInfo, ControllerMethod> requestMapping = new HashMap<>();

        final IndexController indexController = new IndexController();
        final LoginController loginController = new LoginController();
        final RegisterController registerController = new RegisterController();

        requestMapping.put(new RequestMappingInfo("/index", HttpMethod.GET), ControllerMethod.of(indexController, "index"));
        requestMapping.put(new RequestMappingInfo("/login", HttpMethod.GET), ControllerMethod.of(loginController, "loginPage"));
        requestMapping.put(new RequestMappingInfo("/login", HttpMethod.POST), ControllerMethod.of(loginController, "login"));
        requestMapping.put(new RequestMappingInfo("/register", HttpMethod.GET), ControllerMethod.of(registerController, "registerPage"));
        requestMapping.put(new RequestMappingInfo("/register", HttpMethod.POST), ControllerMethod.of(registerController, "register"));

        return requestMapping;
    }
}
