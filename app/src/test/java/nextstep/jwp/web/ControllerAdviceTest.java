package nextstep.jwp.web;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.exception.InternalServerErrorException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpResponse;
import org.junit.jupiter.api.Test;

class ControllerAdviceTest {
    @Test
    void badRequest() {
        String result = ControllerAdvice.handle(new BadRequestException("message"));
        assertThat(result).isEqualTo(HttpResponse.badRequest());
    }

    @Test
    void notFound() {
        String result = ControllerAdvice.handle(new NotFoundException("message"));
        assertThat(result).isEqualTo(HttpResponse.notFound());
    }

    @Test
    void unauthorized() {
        String result = ControllerAdvice.handle(new UnauthorizedException("message"));
        assertThat(result).isEqualTo(HttpResponse.unauthorized());
    }

    @Test
    void internalServerError() {
        String result = ControllerAdvice.handle(new InternalServerErrorException("message"));
        assertThat(result).isEqualTo(HttpResponse.internalServerError());
    }

    @Test
    void exception() {
        String result = ControllerAdvice.handle(new Exception("message"));
        assertThat(result).isEqualTo(HttpResponse.internalServerError());
    }
}
