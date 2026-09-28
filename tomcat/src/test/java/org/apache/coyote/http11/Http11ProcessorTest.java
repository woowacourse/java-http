package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket(String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=session-id",
                "",
                ""
        ));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=session-id ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
            assertThat(resource).isNotNull();

            final var responseBody = resource.readAllBytes();
            final var expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody, StandardCharsets.UTF_8)
            );

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Test
    void 정적_리소스에_맞는_Content_Type을_응답한다() {
        final var contentTypes = Map.of(
                "/css/styles.css", "text/css",
                "/js/scripts.js", "text/javascript",
                "/assets/img/error-404-monochrome.svg", "image/svg+xml"
        );

        contentTypes.forEach((path, contentType) -> {
            final var httpRequest = String.join("\r\n",
                    "GET " + path + " HTTP/1.1",
                    "Host: localhost:8080",
                    "Cookie: JSESSIONID=session-id",
                    "",
                    ""
            );
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            processor.process(socket);

            assertThat(socket.output())
                    .as(path)
                    .contains("Content-Type: " + contentType);
        });
    }

    @Test
    void 두_점이_포함된_정상적인_파일명을_허용한다() {
        final var httpRequest = String.join("\r\n",
                "GET /a..b.js HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=session-id",
                "",
                ""
        );
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .contains("path is valid");
    }

    @Test
    void 로그인에_성공하면_인덱스_페이지로_리다이렉트한다() {
        final var requestBody = "account=gugu&password=password";
        final var socket = new StubSocket(postRequest("/login", requestBody));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    void 로그인에_실패하면_401_페이지로_리다이렉트한다() {
        final var requestBody = "account=gugu&password=wrong";
        final var socket = new StubSocket(postRequest("/login", requestBody));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /401.html");
    }

    @Test
    void 회원가입_페이지를_응답한다() {
        final var socket = new StubSocket("GET /register HTTP/1.1\r\n\r\n");
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .contains("회원 가입");
    }

    @Test
    void 회원가입을_완료하면_회원을_저장하고_인덱스_페이지로_리다이렉트한다() {
        final var requestBody = "account=hello&password=world&email=hello%40example.com";
        final var socket = new StubSocket(postRequest("/register", requestBody));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(InMemoryUserRepository.findByAccount("hello"))
                .isPresent()
                .get()
                .matches(user -> user.checkPassword("world"));
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    void JSESSIONID가_없으면_쿠키를_발급한다() {
        final var socket = new StubSocket("GET /index.html HTTP/1.1\r\n\r\n");
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f\\-]{36}");
    }

    @Test
    void JSESSIONID가_있으면_쿠키를_다시_발급하지_않는다() {
        final var request = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Cookie: yummy_cookie=choco; JSESSIONID=session-id; tasty_cookie=strawberry",
                "",
                ""
        );
        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output()).doesNotContain("Set-Cookie");
    }

    private String postRequest(final String path, final String requestBody) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.length(),
                "Cookie: JSESSIONID=session-id",
                "",
                requestBody
        );
    }
}
