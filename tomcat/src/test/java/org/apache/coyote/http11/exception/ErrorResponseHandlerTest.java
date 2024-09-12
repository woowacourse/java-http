package org.apache.coyote.http11.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ErrorResponseHandlerTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void cleanUp() throws IOException {
        outputStream.close();
    }

    @DisplayName("요청 중 예외가 발생하면 커스텀 오류 코드를 응답한다.")
    @Test
    void exceptionHandler() {
        // given
        ErrorResponseHandler.getInstance().setResponse(new HttpResponse(outputStream));
        String errorMessage = "메서드를 지원하지 않습니다";

        // when
        try {
            throw new RequestException(HttpStatusCode.METHOD_NOT_ALLOWED, errorMessage);
        } catch (RequestException e) {
            String actual = outputStream.toString(StandardCharsets.UTF_8);
            // then
            assertThat(actual).isEqualTo(errorMessage);
        }
    }
}
