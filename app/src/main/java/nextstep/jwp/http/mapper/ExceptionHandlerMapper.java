package nextstep.jwp.http.mapper;

import nextstep.jwp.http.exception.ExceptionHandler;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.mapping.ExceptionHandlerMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExceptionHandlerMapper implements Mapper<ExceptionHandler, RuntimeException> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerMapper.class);

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
