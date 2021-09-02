package nextstep.jwp.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.exception.InternalServerErrorException;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.web.exceptionhandler.BadRequestExceptionHandler;
import nextstep.jwp.web.exceptionhandler.ExceptionHandler;
import nextstep.jwp.web.exceptionhandler.InternalServerErrorExceptionHandler;
import nextstep.jwp.web.exceptionhandler.MethodNotAllowedExceptionHandler;
import nextstep.jwp.web.exceptionhandler.NotFoundExceptionHandler;
import nextstep.jwp.web.exceptionhandler.UnauthorizedExceptionHandler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ControllerAdviceTest {
    public static Stream<Arguments> exceptionHandlerMapper() {
        return Stream.of(
                Arguments.of(new BadRequestException(), BadRequestExceptionHandler.class),
                Arguments.of(new NotFoundException(), NotFoundExceptionHandler.class),
                Arguments.of(new UnauthorizedException(), UnauthorizedExceptionHandler.class),
                Arguments.of(new MethodNotAllowedException(), MethodNotAllowedExceptionHandler.class),
                Arguments.of(new InternalServerErrorException(), InternalServerErrorExceptionHandler.class),
                Arguments.of(new NullPointerException(), InternalServerErrorExceptionHandler.class),
                Arguments.of(new Exception(), InternalServerErrorExceptionHandler.class));
    }

    @ParameterizedTest
    @MethodSource("exceptionHandlerMapper")
    void exceptionMapper(Exception exception, Class<? extends ExceptionHandler> exceptionHandlerClass) {
        ExceptionHandler handler = ControllerAdvice.findExceptionHandler(exception);
        assertThat(handler).isInstanceOf(exceptionHandlerClass);
    }
}
