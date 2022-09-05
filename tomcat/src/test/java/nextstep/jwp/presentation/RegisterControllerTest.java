package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class RegisterControllerTest {

    @Test
    void GET_register_를_호출하면_회원가입_페이지로_이동한다() throws IOException {
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
        final File file = new File(Thread.currentThread().getContextClassLoader().getResource("static/register.html").getFile());
        final byte[] responseBody = Files.readAllBytes(file.toPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: " + responseBody.length + " \r\n" +
            "\r\n" +
            new String(responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void POST_register_를_호출과_회원정보를_입력하면_회원가입이_완료되고_메인페이지로_이동한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 80 ",
            "Content-Type: application/x-www-form-urlencoded ",
            "Accept: */* ",
            "",
            "account=rookie&password=password&email=rookie%40woowahan.com ");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final File file = new File(Thread.currentThread().getContextClassLoader().getResource("static/index.html").getFile());
        final byte[] responseBody = Files.readAllBytes(file.toPath());
        var expected = "HTTP/1.1 302 FOUND \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: " + responseBody.length + " \r\n" +
            "\r\n" +
            new String(responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void POST_register_를_호출과_중복되는_회원정보를_입력하면_회원가입_페이지로_이동한다() throws IOException {

        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 80 ",
            "Content-Type: application/x-www-form-urlencoded ",
            "Accept: */* ",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com ");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final File file = new File(Thread.currentThread().getContextClassLoader().getResource("static/register.html").getFile());
        final byte[] responseBody = Files.readAllBytes(file.toPath());
        var expected = "HTTP/1.1 302 FOUND \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: " + responseBody.length + " \r\n" +
            "\r\n" +
            new String(responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
