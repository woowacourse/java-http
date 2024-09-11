package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class NotFoundControllerTest {

    @DisplayName("존재하지 않는 API를 호출하면 404.html 페이지로 리다이렉트한다.")
    @Test
    void successNotFoundGetTest() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /jazz HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND",
                "Location: /404.html",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }
}
