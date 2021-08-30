package nextstep.joanne.handler;

import nextstep.joanne.http.HttpStatus;
import nextstep.joanne.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    @Test
    @DisplayName("HttpStatus가 UnAuthorized")
    void handleWith401() {
        HttpResponse httpResponse = ErrorHandler.handle(HttpStatus.UNAUTHORIZED);
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("HttpStatus가 InternalServerError")
    void handleWith500() {
        HttpResponse httpResponse = ErrorHandler.handle(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("HttpStatus가 NotFound")
    void handleWith404() {
        HttpResponse httpResponse = ErrorHandler.handle(HttpStatus.NOT_FOUND);
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}