package nextstep.jwp.web;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.ConfigurationException;
import nextstep.jwp.exception.InternalServerError;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.web.model.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ControllerMethod {

    private static final Logger logger = LoggerFactory.getLogger(ControllerMethod.class);

    private final Controller controller;
    private final Method method;

    public ControllerMethod(Controller controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public static ControllerMethod of(Controller controller, String methodName) {
        return new ControllerMethod(controller, extractMethod(controller, methodName));
    }

    private static Method extractMethod(Controller controller, String methodName) {
        try {
            return controller.getClass().getMethod(methodName, HttpRequest.class, HttpSession.class);
        } catch (NoSuchMethodException e) {
            logger.error("Cannot Request Mapping By No Such Method", e);
            throw new ConfigurationException();
        }
    }

    public Object invoke(HttpRequest httpRequest, HttpSession httpSession) {
        try {
            return method.invoke(this.controller, httpRequest, httpSession);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("method invoke error", e);
            throw new InternalServerError();
        }
    }
}
