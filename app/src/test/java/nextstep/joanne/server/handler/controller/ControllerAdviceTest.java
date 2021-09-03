package nextstep.joanne.server.handler.controller;

import nextstep.joanne.server.http.HttpStatus;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.request.RequestLine;
import nextstep.joanne.server.http.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerAdviceTest {
    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() {
        httpRequest = new HttpRequest(
                new RequestLine(null, "index.html", null),
                null,
                null);
    }

    private void handleError(HttpStatus httpStatus) {
        HttpResponse httpResponse = new HttpResponse();
        handle(httpResponse, httpStatus);
        assertThat(httpResponse.getStatus()).isEqualTo(httpStatus.value());
    }

    private void handle(HttpResponse httpResponse, HttpStatus unauthorized) {
        ControllerAdvice.handle(httpRequest, httpResponse, unauthorized);
    }

    @Test
    @DisplayName("HttpStatus가 UnAuthorized")
    void handleWith401() {
        handleError(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("HttpStatus가 InternalServerError")
    void handleWith500() {
        handleError(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("HttpStatus가 NotFound")
    void handleWith404() {
        handleError(HttpStatus.NOT_FOUND);
    }
}
