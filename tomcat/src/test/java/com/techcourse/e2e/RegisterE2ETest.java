package com.techcourse.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class RegisterE2ETest {

    @Test
    @DisplayName("GET /register 요청에 응답한다.")
    void getRegister() throws IOException {
        // given
        final String httpRequest = """
                GET /register HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                """;

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String responseHeader = """
                HTTP/1.1 200 OK
                Content-Type: text/html;charset=utf-8
                Content-Length:""";
        URL staticResourceUrl = getClass().getClassLoader().getResource("static/register.html");
        File file = new File(staticResourceUrl.getFile());
        String logInPage = new String(Files.readAllBytes(file.toPath()));

        assertThat(socket.output()).startsWith(responseHeader);
        assertThat(socket.output()).contains(logInPage);
    }

    @Test
    @DisplayName("POST /register 요청에 응답한다.")
    void postRegister() {
        // given
        final String httpRequest = """
                POST /register HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                Content-Type: application/x-www-form-urlencoded
                Content-Length: 40
                
                account=hi&password=bye&email=bye@hi.com
                """;

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = """
                HTTP/1.1 302 Found
                Location: index.html
                Set-Cookie:""";

        assertThat(socket.output()).startsWith(expected);
    }
}
