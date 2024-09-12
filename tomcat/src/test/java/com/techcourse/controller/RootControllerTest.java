package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.mapper.RequestMapper;
import org.apache.catalina.Mapper;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class RootControllerTest {

    private final Mapper mapper = RequestMapper.getInstance();

    @DisplayName("루트 페이지를 호출한다.")
    @Test
    void successRootGetTest() {
        String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, mapper);

        processor.process(socket);

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);

    }
}
