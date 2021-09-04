package nextstep.jwp.http.mapper;

import nextstep.jwp.http.exception.ExceptionHandler;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.mapping.ExceptionHandlerMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExceptionResponseMapper implements Mapper<HttpResponseMessage, RuntimeException> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionResponseMapper.class);

    private static final ExceptionResponseMapper instance = new ExceptionResponseMapper();

    private ExceptionResponseMapper() {
    }

    public static ExceptionResponseMapper getInstance() {
        return instance;
    }

    @Override
    public HttpResponseMessage resolve(RuntimeException exception) {
        Map<Class<? extends RuntimeException>, ExceptionHandler> mappings =
                ExceptionHandlerMappings.getInstance().getMappings();
        Class<? extends RuntimeException> exceptionType = exception.getClass();
        if (mappings.containsKey(exceptionType)) {
            log.debug("예외 핸들링 성공 {}", exception.getMessage());
            return mappings.get(exceptionType).run(exception);
        }
        throw exception;
    }
}
