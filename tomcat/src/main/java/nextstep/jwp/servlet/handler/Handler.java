package nextstep.jwp.servlet.handler;

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

    public boolean hasReturnTypeOf(Class<?> clazz) {
        return method.getReturnType().equals(clazz);
    }

    public Object handle(Object arg) {
        try {
            return handleWithValidArgs(arg);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw extractHandlerExceptionCause(e);
        }
    }

    private Object handleWithValidArgs(Object arg) throws IllegalAccessException, InvocationTargetException {
        if (method.getParameterCount() == 0) {
            return method.invoke(controller);
        }
        return method.invoke(controller, arg);
    }

    private HttpException extractHandlerExceptionCause(ReflectiveOperationException exception) {
        final var cause = exception.getCause();
        if (cause instanceof HttpException) {
            return (HttpException) cause;
        }
        return new HttpException(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
