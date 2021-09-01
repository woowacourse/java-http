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

    @Test
    @DisplayName("HttpStatus가 UnAuthorized")
    void handleWith401() {
        HttpResponse httpResponse = new HttpResponse();
        ControllerAdvice.handle(httpRequest, httpResponse, HttpStatus.UNAUTHORIZED);
        assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("HttpStatus가 InternalServerError")
    void handleWith500() {
        HttpResponse httpResponse = new HttpResponse();
        ControllerAdvice.handle(httpRequest, httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("HttpStatus가 NotFound")
    void handleWith404() {
        HttpResponse httpResponse = new HttpResponse();
        ControllerAdvice.handle(httpRequest, httpResponse, HttpStatus.NOT_FOUND);
        assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}