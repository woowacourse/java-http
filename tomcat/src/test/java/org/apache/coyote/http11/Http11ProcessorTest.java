package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import org.apache.catalina.connector.RequestHandler;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void processesRequestWithApplicationProvidedMapping() {
        final var socket = new StubSocket("GET /custom HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");

        new RequestHandler(path -> {
            assertThat(path).isEqualTo("/custom");
            return request -> HttpResponse.create("200 OK", "text/plain", "custom response");
        }).handle(new Http11Processor(socket));

        assertThat(socket.output()).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/plain;charset=utf-8",
                "Content-Length: 15 ",
                "",
                "custom response"));
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket();

        // when
        new RequestHandler(new RequestMapping()).handle(new Http11Processor(socket));

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);

        // when
        new RequestHandler(new RequestMapping()).handle(new Http11Processor(socket));

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: " + responseBody.length + " \r\n" +
                "\r\n"+
                new String(responseBody, StandardCharsets.UTF_8);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
