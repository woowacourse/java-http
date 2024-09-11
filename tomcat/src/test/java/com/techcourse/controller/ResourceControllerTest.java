package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class ResourceControllerTest {

    @DisplayName("정적 리소스를 요청한다.")
    @Test
    void successStaticResourceGetTest() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String content = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        String contentLength = Integer.toString(content.getBytes(StandardCharsets.UTF_8).length);

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/css;charset=utf-8",
                "Content-Length: " + contentLength,
                "",
                content);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
