package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class RegisterHandlerTest {

    @Test
    @DisplayName("GET '/register' 요청에 대한 응답이 정상적으로 처리된다.")
    void register() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 4319 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST '/register' 요청에 대한 응답이 정상적으로 처리된다.")
    void register_post() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 33",
                "",
                "account=gooreum&password=password"
        );
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertAll(
                () -> assertThat(socket.output()).isEqualTo(expected),
                () -> assertThat(InMemoryUserRepository.findByAccount("gooreum")).isPresent()
        );
    }

    @Test
    @DisplayName("POST '/register' 요청 시 계정이 이미 존재하면 '/400.html'로 리다이렉트한다.")
    void register_post_duplicate() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=password"
        );
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /400.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertAll(
                () -> assertThat(InMemoryUserRepository.findByAccount("gugu")).isPresent(),
                () -> assertThat(socket.output()).isEqualTo(expected)
        );
    }
}
