package nextstep.jwp.handler;

import static org.apache.coyote.http11.HttpStatus.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UncheckedServletException;
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

        exceptionMapping.put(IllegalArgumentException.class, NOT_FOUND);
        exceptionMapping.put(UncheckedServletException.class, NOT_FOUND);
        exceptionMapping.put(UnauthorizedException.class, UNAUTHORIZED);

        return new ServletAdvice(exceptionMapping);
    }

    public <T extends Exception> HttpStatus getExceptionStatusCode(final Class<T> exception) throws IOException {
        if (isUnhandledError(exception)) {
            return SERVER_ERROR;
        }

        return exceptionMapping.get(exception);
    }

    private <T extends Exception> boolean isUnhandledError(final Class<T> exception) {
        return !exceptionMapping.containsKey(exception);
    }
}
