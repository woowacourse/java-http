package nextstep.jwp.web;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.exception.InternalServerErrorException;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.web.exceptionhandler.BadRequestExceptionHandler;
import nextstep.jwp.web.exceptionhandler.ExceptionHandler;
import nextstep.jwp.web.exceptionhandler.InternalServerErrorExceptionHandler;
import nextstep.jwp.web.exceptionhandler.MethodNotAllowedExceptionHandler;
import nextstep.jwp.web.exceptionhandler.NotFoundExceptionHandler;
import nextstep.jwp.web.exceptionhandler.UnauthorizedExceptionHandler;

public class ControllerAdvice {

    private static final Map<Class<? extends Exception>, ExceptionHandler> handlers = new HashMap<>();

    static {
        handlers.put(BadRequestException.class, new BadRequestExceptionHandler());
        handlers.put(NotFoundException.class, new NotFoundExceptionHandler());
        handlers.put(UnauthorizedException.class, new UnauthorizedExceptionHandler());
        handlers.put(MethodNotAllowedException.class, new MethodNotAllowedExceptionHandler());
        handlers.put(InternalServerErrorException.class, new InternalServerErrorExceptionHandler());
        handlers.put(Exception.class, new InternalServerErrorExceptionHandler());
    }

    public static String handle(Exception exception) {
        ExceptionHandler exceptionHandler = handlers.get(exception.getClass());

        return exceptionHandler.handle(exception);
    }
}
