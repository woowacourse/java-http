package nextstep.jwp.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import nextstep.jwp.exception.InternalServerException;
import nextstep.jwp.exception.badRequest.BadRequestException;
import nextstep.jwp.exception.notfound.NotFoundException;
import nextstep.jwp.exception.unauthorized.UnAuthorizedException;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ControllerAdvice.class);

    private static final Map<Class<? extends Exception>, Method> HANDLER_MAPPING = new LinkedHashMap<>();

    static {
        try {
            HANDLER_MAPPING.put(BadRequestException.class, getHandleMethod(BadRequestException.class));
            HANDLER_MAPPING.put(UnAuthorizedException.class, getHandleMethod(UnAuthorizedException.class));
            HANDLER_MAPPING.put(NotFoundException.class, getHandleMethod(NotFoundException.class));
            HANDLER_MAPPING.put(IOException.class, getHandleMethod(IOException.class));
            HANDLER_MAPPING.put(RuntimeException.class, getHandleMethod(RuntimeException.class));
        } catch (NoSuchMethodException e) {
            throw new InternalServerException(e.getMessage() + " method를 찾을 수 없습니다.");
        }
    }

    private static Method getHandleMethod(Class<? extends Exception> exceptionClass) throws NoSuchMethodException {
        return ControllerAdvice.class.getDeclaredMethod("handle", HttpResponse.class, exceptionClass);
    }

    public void handleException(HttpResponse httpResponse, Exception exception) {
        try {
            Class<? extends Exception> exceptionClass = exception.getClass();
            Method method = getMethod(exceptionClass);
            method.invoke(this, httpResponse, exceptionClass.cast(exception));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new InternalServerException(exception.getMessage() + " 예외를 처리할 수 없습니다.");
        }
    }

    private Method getMethod(Class<? extends Exception> exceptionClass) {
        return HANDLER_MAPPING.entrySet()
                .stream()
                .filter(handlerMapping -> handlerMapping.getKey().isAssignableFrom(exceptionClass))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new InternalServerException("처리할 수 있는 예외가 아닙니다."));
    }

    public void handle(HttpResponse httpResponse, BadRequestException exception) {
        log.warn(exception.getMessage());
        httpResponse.badRequest();
    }

    public void handle(HttpResponse httpResponse, UnAuthorizedException exception) {
        log.warn(exception.getMessage());
        httpResponse.unAuthorized();
    }

    public void handle(HttpResponse httpResponse, NotFoundException exception) {
        log.warn(exception.getMessage());
        httpResponse.notFound();
    }

    public void handle(HttpResponse httpResponse, RuntimeException exception) {
        log.warn(exception.getMessage());
        httpResponse.badRequest();
    }

    public void handle(HttpResponse httpResponse, IOException exception) {
        log.warn(exception.getMessage());
        httpResponse.notFound();
    }
}
