package nextstep.jwp.framework.mapper;

import nextstep.jwp.application.mapping.ExceptionHandlerMappings;
import nextstep.jwp.framework.exception.ExceptionHandler;

import java.util.Map;

public class ExceptionHandlerMapper implements Mapper<ExceptionHandler, RuntimeException> {

    private static final ExceptionHandlerMapper instance = new ExceptionHandlerMapper();

    private ExceptionHandlerMapper() {
    }

    public static ExceptionHandlerMapper getInstance() {
        return instance;
    }

    @Override
    public ExceptionHandler resolve(RuntimeException exception) {
        Map<Class<? extends RuntimeException>, ExceptionHandler> mappings =
                ExceptionHandlerMappings.getInstance().getMappings();
        Class<? extends RuntimeException> exceptionType = exception.getClass();
        if (mappings.containsKey(exceptionType)) {
            return mappings.get(exceptionType);
        }
        throw exception;
    }
}
