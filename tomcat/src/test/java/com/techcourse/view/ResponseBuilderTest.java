package com.techcourse.view;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.exception.StaticResourceNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseBuilderTest {

    @Test
    @DisplayName("예외에 대한 문자열 응답을 생성한다.")
    void buildExceptionResponse() throws IOException {
        // given
        final var responseBuilder = new ResponseBuilder();
        final var exception = new StaticResourceNotFoundException();

        // when
        final var response = responseBuilder.buildExceptionResponse(exception);

        // then
        final var expected = """
                HTTP/1.1 404 Not Found
                Content-Type: text/html;charset=utf-8
                Content-Length:""";

        assertThat(response).startsWith(expected);
    }
}
