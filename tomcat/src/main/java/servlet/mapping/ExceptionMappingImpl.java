package servlet.mapping;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.handler.ExceptionHandler;

public class ExceptionMappingImpl implements ExceptionMapping {


    private static final Logger LOG = LoggerFactory.getLogger(ExceptionMappingImpl.class);

    private final List<ExceptionHandler> exceptionHandlers;
    private final ExceptionHandler baseHandler;

    public ExceptionMappingImpl(List<ExceptionHandler> exceptionHandlers,
                                ExceptionHandler baseHandler) {
        this.exceptionHandlers = exceptionHandlers;
        this.baseHandler = baseHandler;
    }

    @Override
    public ResponseEntity map(Exception exception) {
        LOG.error(String.valueOf(exception.getClass()), exception);

        ExceptionHandler exceptionHandler = findHandler(exception);

        ResponseEntity entity = new ResponseEntity();
        exceptionHandler.service(exception, entity);
        return entity;
    }

    private ExceptionHandler findHandler(Exception exception) {
        return exceptionHandlers.stream()
                .filter(element -> isContains(exception, element))
                .findFirst()
                .orElse(baseHandler);
    }

    private boolean isContains(Exception exception, ExceptionHandler element) {
        List<Class<? extends Exception>> exceptionClass = element.getExceptionClass();
        return exceptionClass.contains(exception.getClass());
    }
}
