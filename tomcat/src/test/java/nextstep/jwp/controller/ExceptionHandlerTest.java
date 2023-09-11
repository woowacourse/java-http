package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.exception.AuthException;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExceptionHandlerTest {

    @Test
    void AuthException이면_Unauthorized로_처리한다() {
        // given
        ExceptionHandler exceptionHandler = new ExceptionHandler();
        HttpResponse httpResponse = new HttpResponse("Http/1.1");

        // when
        exceptionHandler.handle(httpResponse, new AuthException());

        // then
        assertThat(httpResponse.responseMessage()).contains("Unauthorized");
    }

    @Test
    void NotFoundException이면_NotFound로_처리한다() {
        // given
        ExceptionHandler exceptionHandler = new ExceptionHandler();
        HttpResponse httpResponse = new HttpResponse("Http/1.1");

        // when
        exceptionHandler.handle(httpResponse, new NotFoundException());

        // then
        assertThat(httpResponse.responseMessage()).contains("Not Found");
    }
}
