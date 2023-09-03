package org.apache.coyote.http11.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertAll;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 생성 성공 테스트")
    void from() throws IOException {
        // given
        Socket socket = new StubSocket("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");

        final InputStream inputStream = socket.getInputStream();

        //when
        final HttpRequest request = HttpRequest.from(inputStream);

        //then
        assertAll(
                () -> Assertions.assertThat(request.getRequestURL().getUrl()).isEqualTo("/"),
                () -> Assertions.assertThat(request.getRequestURL().getRequestParam()).isEmpty(),
                () -> Assertions.assertThat(request.getRequestHeaders().getValue("Host")).isEqualTo("localhost:8080")
        );
    }
}
