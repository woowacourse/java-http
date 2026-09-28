package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpErrorHandlerTest {

    @Test
    @DisplayName("컨트롤러 처리 중 발생한 IOException은 서버 오류로 응답한다")
    void respondsWithInternalServerErrorWhenControllerIOExceptionOccurs() throws IOException {
        final HttpErrorHandler handler = new HttpErrorHandler();
        final HttpResponse response = handler.handle("HTTP/1.1", new IOException("파일 읽기 실패"));
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.write(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 500 Internal Server Error ");
    }

    @Test
    @DisplayName("커스텀 HTTP 예외가 아닌 IllegalArgumentException은 내부 오류로 응답한다")
    void respondsWithInternalServerErrorWhenUnexpectedIllegalArgumentExceptionOccurs() throws IOException {
        final HttpErrorHandler handler = new HttpErrorHandler();
        final HttpResponse response = handler.handle("HTTP/1.1", new IllegalArgumentException("내부 오류"));
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.write(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 500 Internal Server Error ");
    }
}
