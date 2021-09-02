package nextstep.jwp.web;

import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.ConfigurationException;
import nextstep.jwp.request.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestMappingHandlerConfig {

    private static final Logger logger = LoggerFactory.getLogger(RequestMappingHandlerConfig.class);

    private RequestMappingHandlerConfig() {
    }

    public static Map<RequestMapping, ControllerMethod> config() {
        final Map<RequestMapping, ControllerMethod> requestMapping = new HashMap<>();

        final IndexController indexController = new IndexController();
        final LoginController loginController = new LoginController();
        final RegisterController registerController = new RegisterController();

        try {
            requestMapping.put(new RequestMapping("/index", HttpMethod.GET), ControllerMethod.of(indexController, "index"));
            requestMapping.put(new RequestMapping("/login", HttpMethod.GET), ControllerMethod.of(loginController, "loginPage"));
            requestMapping.put(new RequestMapping("/login", HttpMethod.POST), ControllerMethod.of(loginController, "login"));
            requestMapping.put(new RequestMapping("/register", HttpMethod.GET), ControllerMethod.of(registerController, "registerPage"));
            requestMapping.put(new RequestMapping("/register", HttpMethod.POST), ControllerMethod.of(registerController, "register"));
        } catch (NoSuchMethodException e) {
            logger.error("cannot request mapping", e);
            throw new ConfigurationException();
        }
        return requestMapping;
    }
}
