package nextstep.jwp.servlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpStatus;

public class Handler {

    private final Method method;
    private final Object controller;

    public Handler(Method method, Object controller) {
        this.method = method;
        this.controller = controller;
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public Object handle(Object arg) {
        try {
            return handleWithValidArgs(arg);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new HttpException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Object handleWithValidArgs(Object arg) throws IllegalAccessException, InvocationTargetException {
        if (method.getParameterCount() == 0) {
            return method.invoke(controller);
        }
        return method.invoke(controller, arg);
    }
}
