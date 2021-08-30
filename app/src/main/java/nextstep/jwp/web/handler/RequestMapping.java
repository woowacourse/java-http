package nextstep.jwp.web.handler;

import static nextstep.jwp.web.http.request.HttpMethod.*;

import java.util.Map;
import nextstep.jwp.application.controller.LoginController;
import nextstep.jwp.application.controller.RegisterController;
import nextstep.jwp.application.controller.WelcomeController;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.mvc.controller.Controller;
import nextstep.jwp.web.mvc.controller.ExceptionController;
import nextstep.jwp.web.mvc.controller.NotFoundController;

public class RequestMapping {

    private static final Map<MethodUrl, Controller> myMap;
    private static final Controller NOT_FOUND_CACHE = new NotFoundController();
    private static final ExceptionController ERROR_CACHE = new ExceptionController();

    private final Map<MethodUrl, Controller> controllerMap;

    static {
        myMap = Map.of(
            new MethodUrl(GET, "/index"), new WelcomeController(),
            new MethodUrl(GET, "/login"), new LoginController(),
            new MethodUrl(POST, "/login"), new LoginController(),
            new MethodUrl(GET, "/register"), new RegisterController(),
            new MethodUrl(POST, "/register"), new RegisterController()
        );
    }

    public RequestMapping() {
        this(myMap);
    }

    public RequestMapping(
        Map<MethodUrl, Controller> myMap) {
        this.controllerMap = myMap;
    }

    public Controller getController(MethodUrl methodUrl) {
        return controllerMap.getOrDefault(methodUrl, NOT_FOUND_CACHE);
    }

    public ExceptionController errorController() {
        return ERROR_CACHE;
    }
}
