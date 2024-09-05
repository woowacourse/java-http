package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@DisplayName("HTTP 프로세서 테스트")
class Http11ProcessorTest {

    @DisplayName("HTTP 요청에 대하여, HTTP 응답을 반환한다.")
    @Test
    void index() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();
        String expectedRequestLine = "HTTP/1.1 " + HttpStatusCode.OK.toStatus();
        String expectedContentType = "Content-Type: " + MimeType.HTML.getContentType();

        assertAll(
                () -> assertThat(output).contains(expectedRequestLine),
                () -> assertThat(output).contains(expectedContentType)
        );
    }
}
