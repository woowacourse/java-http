package nextstep.jwp.mvc;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.InternalServerError;
import nextstep.jwp.request.HttpRequest;
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

    public static ControllerMethod of(Controller controller, String methodName) throws NoSuchMethodException {
        return new ControllerMethod(controller, controller.getClass().getMethod(methodName, HttpRequest.class));
    }

    public Object invoke(HttpRequest httpRequest) {
        try {
            return method.invoke(this.controller, httpRequest);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("method invoke error", e);
            throw new InternalServerError();
        }
    }

    public Controller getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
