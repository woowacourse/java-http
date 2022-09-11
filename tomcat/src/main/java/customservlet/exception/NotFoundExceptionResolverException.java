package customservlet.exception;

import org.apache.coyote.http11.exception.NotFoundException;

public class NotFoundExceptionResolverException extends NotFoundException {

    public NotFoundExceptionResolverException(final Class<? extends Exception> exceptionClass) {
        super(exceptionClass.getName() + "를 처리할 Resolver가 존재하지 않습니다.");
    }
}
