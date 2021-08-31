package nextstep.jwp.mvc;

import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.InternalServerError;
import nextstep.jwp.request.HttpMethod;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMappingHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestMappingHandler.class);

    private static final RequestMappingHandler INSTANCE = new RequestMappingHandler(new ConcurrentHashMap<>());

    static {
        final IndexController indexController = new IndexController();
        final LoginController loginController = new LoginController();
        final RegisterController registerController = new RegisterController();

        try {
            INSTANCE.requestMapping.put(new RequestMapping("/index", HttpMethod.GET), ControllerMethod.of(indexController, "index"));
            INSTANCE.requestMapping.put(new RequestMapping("/login", HttpMethod.GET), ControllerMethod.of(loginController, "loginPage"));
            INSTANCE.requestMapping.put(new RequestMapping("/login", HttpMethod.POST), ControllerMethod.of(loginController, "login"));
            INSTANCE.requestMapping.put(new RequestMapping("/register", HttpMethod.GET), ControllerMethod.of(registerController, "registerPage"));
            INSTANCE.requestMapping.put(new RequestMapping("/register", HttpMethod.POST), ControllerMethod.of(registerController, "register"));
        } catch (NoSuchMethodException e) {
            logger.error("can't request mapping", e);
            throw new InternalServerError();
        }
    }

    private final Map<RequestMapping, ControllerMethod> requestMapping;

    private RequestMappingHandler(Map<RequestMapping, ControllerMethod> requestMapping) {
        this.requestMapping = requestMapping;
    }

    public Optional<ControllerMethod> getControllerMethod(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        return Optional.ofNullable(requestMapping.get(RequestMapping.of(requestLine)));
    }

    public static RequestMappingHandler getInstance() {
        return INSTANCE;
    }
}
