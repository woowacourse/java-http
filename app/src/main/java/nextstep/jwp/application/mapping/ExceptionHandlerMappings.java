package nextstep.jwp.application.mapping;

import nextstep.jwp.application.exception.UnauthorizedException;
import nextstep.jwp.application.exception.handler.HtmlNotFoundExceptionHandler;
import nextstep.jwp.application.exception.handler.RuntimeExceptionHandler;
import nextstep.jwp.application.exception.handler.StaticResourceNotFoundExceptionHandler;
import nextstep.jwp.application.exception.handler.UnauthorizedExceptionHandler;
import nextstep.jwp.application.exception.handler.UriMappingNotFoundExceptionHandler;
import nextstep.jwp.framework.exception.ExceptionHandler;
import nextstep.jwp.framework.exception.HtmlNotFoundException;
import nextstep.jwp.framework.exception.StaticResourceNotFoundException;
import nextstep.jwp.framework.exception.UriMappingNotFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExceptionHandlerMappings {

    private static final ExceptionHandlerMappings instance = new ExceptionHandlerMappings();

    private final Map<Class<? extends RuntimeException>, ExceptionHandler> mappings = new LinkedHashMap<>();

    private ExceptionHandlerMappings() {
        mappings.put(UnauthorizedException.class, new UnauthorizedExceptionHandler());
        mappings.put(UriMappingNotFoundException.class, new UriMappingNotFoundExceptionHandler());
        mappings.put(HtmlNotFoundException.class, new HtmlNotFoundExceptionHandler());
        mappings.put(StaticResourceNotFoundException.class, new StaticResourceNotFoundExceptionHandler());
        mappings.put(RuntimeException.class, new RuntimeExceptionHandler());
    }

    public static ExceptionHandlerMappings getInstance() {
        return instance;
    }

    public Map<Class<? extends RuntimeException>, ExceptionHandler> getMappings() {
        return mappings;
    }
}
