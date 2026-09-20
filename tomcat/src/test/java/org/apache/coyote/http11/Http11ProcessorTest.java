package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Content-Length: 12")
                .contains("Hello world!");
    }

    @Test
    void index() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final byte[] expectedBody = Files.readAllBytes(Path.of(resource.toURI()));

        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Content-Length: " + expectedBody.length)
                .contains(new String(expectedBody, StandardCharsets.UTF_8));
    }

    @Test
    void 로그인에_성공하면_index로_리다이렉트한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(httpRequest);

        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html")
                .contains("Content-Length: 0");
    }


    @Test
    void 로그인에_실패하면_401페이지로_리다이렉트한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=wrong HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(httpRequest);

        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /401.html")
                .contains("Content-Length: 0");
    }

    @Test
    void POST_방식으로_로그인에_성공하면_index로_리다이렉트한다() {
        // given
        final String body =
                "account=gugu&password=password";

        final String httpRequest =
                String.join("\r\n",
                        "POST /login HTTP/1.1",
                        "Host: localhost:8080",
                        "Content-Length: "
                                + body.getBytes(StandardCharsets.UTF_8).length,
                        "Content-Type: application/x-www-form-urlencoded",
                        "",
                        body
                );

        final var socket =
                new StubSocket(httpRequest);

        final var processor =
                new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    void POST_방식으로_회원가입한다() {
        // given
        //브라우저에서는 인코딩한 값으로 보내서 @ 가 아니라 %40으로씀. @를 쓰면 디코딩 감지 테스트가 안됨.
        final String body =
                "account=moca&password=1234&email=moca%40email.com";

        final String httpRequest =
                String.join("\r\n",
                        "POST /register HTTP/1.1",
                        "Host: localhost:8080",
                        "Content-Length: "
                                + body.getBytes(StandardCharsets.UTF_8).length,
                        "Content-Type: application/x-www-form-urlencoded",
                        "",
                        body
                );

        final var socket =
                new StubSocket(httpRequest);

        final var processor =
                new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(
                InMemoryUserRepository
                        .findByAccount("moca")
        ).isPresent();

        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    void JSESSIONID가_없으면_새로운_쿠키를_응답한다() {
        final String httpRequest =
                String.join("\r\n",
                        "GET / HTTP/1.1",
                        "Host: localhost:8080",
                        "",
                        ""
                );

        final var socket =
                new StubSocket(httpRequest);

        final var processor =
                new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void JSESSIONID가_있으면_새로운_쿠키를_응답하지_않는다() {
        final String httpRequest =
                String.join("\r\n",
                        "GET / HTTP/1.1",
                        "Host: localhost:8080",
                        "Cookie: JSESSIONID=existing-session",
                        "",
                        ""
                );

        final var socket =
                new StubSocket(httpRequest);

        final var processor =
                new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .doesNotContain("Set-Cookie: JSESSIONID=");
    }


}
