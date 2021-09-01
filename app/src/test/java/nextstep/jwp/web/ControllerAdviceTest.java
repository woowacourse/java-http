package nextstep.jwp.web;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.exception.InternalServerErrorException;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpResponse;
import org.junit.jupiter.api.Test;

class ControllerAdviceTest {
    @Test
    void badRequest() {
        String result = ControllerAdvice.handle(new BadRequestException());
        assertThat(result).isEqualTo(HttpResponse.badRequest());
    }

    @Test
    void notFound() {
        String result = ControllerAdvice.handle(new NotFoundException());
        assertThat(result).isEqualTo(HttpResponse.notFound());
    }

    @Test
    void unauthorized() {
        String result = ControllerAdvice.handle(new UnauthorizedException());
        assertThat(result).isEqualTo(HttpResponse.unauthorized());
    }

    @Test
    void methodNotAllowed() {
        String result = ControllerAdvice.handle(new MethodNotAllowedException());
        assertThat(result).isEqualTo(HttpResponse.methodNotAllowed());
    }

    @Test
    void internalServerError() {
        String result = ControllerAdvice.handle(new InternalServerErrorException());
        assertThat(result).isEqualTo(HttpResponse.internalServerError());
    }

    @Test
    void nullPointerException() {
        String result = ControllerAdvice.handle(new NullPointerException());
        assertThat(result).isEqualTo(HttpResponse.internalServerError());
    }

    @Test
    void exception() {
        String result = ControllerAdvice.handle(new Exception());
        assertThat(result).isEqualTo(HttpResponse.internalServerError());
    }
}
