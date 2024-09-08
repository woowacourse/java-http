package org.apache.coyote.http11.error;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.apache.coyote.http11.error.errorhandler.Error401Handler;
import org.apache.coyote.http11.error.errorhandler.ErrorHandler;
import org.apache.coyote.http11.response.HttpResponse;

public enum ErrorHandlerMapper {

    ERROR401(SecurityException.class, new Error401Handler());

    private Class<? extends Exception> exceptionClass;
    private ErrorHandler errorHandler;

    ErrorHandlerMapper(Class<? extends Exception> exceptionClass, ErrorHandler errorHandler) {
        this.exceptionClass = exceptionClass;
        this.errorHandler = errorHandler;
    }

    public static boolean hasErrorHandler(Class<? extends Exception> exception) {
        return Stream.of(values())
                .anyMatch(handler -> handler.exceptionClass == exception);
    }

    public static HttpResponse handleError(Class<? extends Exception> exception) {
        return Stream.of(values())
                .filter(handler -> handler.exceptionClass == exception)
                .findAny()
                .orElseThrow(()-> new NoSuchElementException("에러 핸들러가 존재하지 않습니다."))
                .errorHandler.handle();
    }
}
