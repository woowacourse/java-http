package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.response.formatter.HttpResponseMessageWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpResponseMessageWriter 테스트")
class HttpResponseMessageWriterTest {

    @Test
    void HttpResponse_메세지_작성_테스트() throws IOException {
        // given
        final var socket = new StubSocket();
        final OutputStream outputStream = socket.getOutputStream();
        final HttpResponse httpResponse = new HttpResponse();
        httpResponse.updateHttpResponseStatusLineByStatus(HttpStatus.OK);
        httpResponse.setHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.setBody("Hello world!");

        // when
        HttpResponseMessageWriter.writeHttpResponse(httpResponse, outputStream);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(socket.output()).isEqualTo(expected);
    }
}
