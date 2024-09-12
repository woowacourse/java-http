package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.catalina.route.DefaultDispatcher;
import org.apache.catalina.route.RequestMapper;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class NotFoundControllerTest {

    @DisplayName("존재하지 않는 리소스를 요청하면 /404.html 로 리다이렉트한다.")
    @Test
    void successNotFoundGetTest() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /notfound HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "", "");

        // when
        StubSocket socket = new StubSocket(httpRequest);
        DefaultDispatcher dispatcher = new DefaultDispatcher(new RequestMapper());
        dispatcher.setNotFoundHandler(NotFoundController.class);
        Http11Processor processor = new Http11Processor(socket, dispatcher);
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/404.html");
        String content = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        String contentLength = Integer.toString(content.getBytes(StandardCharsets.UTF_8).length);
        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Length: " + contentLength,
                "Content-Type: text/html;charset=utf-8",
                "",
                content);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
