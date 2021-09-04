package nextstep.jwp.mapping;

import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.handler.HtmlNotFoundExceptionHandler;
import nextstep.jwp.exception.handler.RuntimeExceptionHandler;
import nextstep.jwp.exception.handler.StaticResourceNotFoundExceptionHandler;
import nextstep.jwp.exception.handler.UnauthorizedExceptionHandler;
import nextstep.jwp.exception.handler.UriMappingNotFoundExceptionHandler;
import nextstep.jwp.http.exception.ExceptionHandler;
import nextstep.jwp.http.exception.HtmlNotFoundException;
import nextstep.jwp.http.exception.StaticResourceNotFoundException;
import nextstep.jwp.http.exception.UriMappingNotFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExceptionHandlerMappings {

    private static final ExceptionHandlerMappings instance = new ExceptionHandlerMappings();

    private final Map<Class<? extends RuntimeException>, ExceptionHandler> mappings = new LinkedHashMap<>();

    {
        mappings.put(UnauthorizedException.class, new UnauthorizedExceptionHandler());
        mappings.put(UriMappingNotFoundException.class, new UriMappingNotFoundExceptionHandler());
        mappings.put(HtmlNotFoundException.class, new HtmlNotFoundExceptionHandler());
        mappings.put(StaticResourceNotFoundException.class, new StaticResourceNotFoundExceptionHandler());
        mappings.put(RuntimeException.class, new RuntimeExceptionHandler());
    }

    private ExceptionHandlerMappings() {
    }

    public static ExceptionHandlerMappings getInstance() {
        return instance;
    }

    public Map<Class<? extends RuntimeException>, ExceptionHandler> getMappings() {
        return mappings;
    }
}
