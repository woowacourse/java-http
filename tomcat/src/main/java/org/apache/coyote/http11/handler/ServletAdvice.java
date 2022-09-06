package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;

public class ServletAdvice {

    private final Map<Class<? extends Exception>, HttpStatus> exceptionMapping;

    public ServletAdvice(final Map<Class<? extends Exception>, HttpStatus> exceptionMapping) {
        this.exceptionMapping = exceptionMapping;
    }

    public static ServletAdvice init() {
        final Map<Class<? extends Exception>, HttpStatus> exceptionMapping = new HashMap<>();

        exceptionMapping.put(IllegalArgumentException.class, HttpStatus.NOT_FOUND);

        return new ServletAdvice(exceptionMapping);
    }

    public <T extends Exception> ResponseEntity handleException(final Class<T> exception) throws IOException {
        if (isUnhandledError(exception)) {
            return FileHandler.createErrorFileResponse(HttpStatus.SERVER_ERROR);
        }
        
        final HttpStatus httpStatus = exceptionMapping.get(exception);
        return FileHandler.createErrorFileResponse(httpStatus);
    }

    private <T extends Exception> boolean isUnhandledError(final Class<T> exception) {
        return !exceptionMapping.containsKey(exception);
    }
}
