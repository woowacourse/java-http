package nextstep.jwp.handler;

import static org.apache.coyote.http11.HttpStatus.NOT_FOUND;
import static org.apache.coyote.http11.HttpStatus.SERVER_ERROR;
import static org.apache.coyote.http11.HttpStatus.UNAUTHORIZED;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class ControllerAdvice {

    private static final Map<Class<? extends RuntimeException>, HttpStatus> exceptionMapping = new HashMap<>();

    static {
        exceptionMapping.put(IllegalArgumentException.class, NOT_FOUND);
        exceptionMapping.put(UncheckedServletException.class, NOT_FOUND);
        exceptionMapping.put(IllegalStateException.class, NOT_FOUND);
        exceptionMapping.put(UnauthorizedException.class, UNAUTHORIZED);
    }

    public <T extends Exception> ResponseEntity handleException(final Class<T> exception) {
        final HttpStatus errorStatus = getExceptionStatusCode(exception);
        return ResponseEntity.createErrorRedirectResponse(HttpStatus.FOUND, errorStatus.getStatusCode() + ".html");
    }

    private  <T extends Exception> HttpStatus getExceptionStatusCode(final Class<T> exception) {
        if (isUnhandledError(exception)) {
            return SERVER_ERROR;
        }
        return exceptionMapping.get(exception);
    }

    private <T extends Exception> boolean isUnhandledError(final Class<T> exception) {
        return !exceptionMapping.containsKey(exception);
    }
}
