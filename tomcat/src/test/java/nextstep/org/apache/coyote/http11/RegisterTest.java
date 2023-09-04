package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterTest {

    @Test
    void 회원가입_페이지에_접속_테스트() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 회원가_성공_테스트() {
        // given
        String requestBody = "account=gitchan&password=password&email=gitchan@gmail.com";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html";

        assertThat(socket.output()).contains(expected);
    }
}
