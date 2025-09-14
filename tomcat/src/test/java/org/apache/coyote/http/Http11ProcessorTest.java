package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("루트 경로 요청 시 Hello world 응답")
    void process() {
        // given
        final String request = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final StubSocket socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/html;charset=UTF-8\r\n")
                .contains("Content-Length: 12\r\n")
                .endsWith("\r\nHello world!");
    }

    @Test
    @DisplayName("index.html 파일 요청 시 정적 파일 응답")
    void index() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();

        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        assertThat(output).contains("HTTP/1.1 200 OK")
                .contains("Content-Type: text/html;charset=UTF-8")
                .contains("Content-Length: 5564")
                .contains(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    @Test
    @DisplayName("CSS 파일 요청 시 Content-Type: text/css 응답")
    void css() {
        // given
        final String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertSoftly(softly -> {
            softly.assertThat(output).contains("Content-Type: text/css");
            softly.assertThat(output).contains("HTTP/1.1 200 OK");
        });
    }

    @Test
    @DisplayName("JS 파일 요청 시 Content-Type: application/javascript 응답")
    void javascript() {
        // given
        final String request = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: application/javascript ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertSoftly(softly -> {
            softly.assertThat(output).contains("Content-Type: application/javascript");
            softly.assertThat(output).contains("HTTP/1.1 200 OK");
        });
    }

    @Test
    @DisplayName("로그인 페이지 Query String 파싱하여 로그인 처리")
    void loginQueryString() {
        // given
        final String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 200 OK");
            softly.assertThat(output).contains("Content-Type: text/html;charset=UTF-8");
        });
    }

    @Test
    @DisplayName("존재하지 않는 파일 요청 시 Not Found 응답")
    void notFound() {
        // given
        final String request = String.join("\r\n",
                "GET /nonexistent.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1");
            softly.assertThat(output).contains("Not Found");
        });
    }

    @Test
    @DisplayName("확장자 없이 HTML 파일 요청 시 자동으로 .html 확장자 추가")
    void loginWithoutExtension() {
        // given
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 200 OK");
            softly.assertThat(output).contains("Content-Type: text/html;charset=UTF-8");
        });
    }

    @Test
    @DisplayName("POST 요청 본문 파싱 및 처리")
    void parsePostRequestBody() {
        // given
        final String request = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 51",
                "",
                "account=userPa&password=1234&email=user%40example.com");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();

        assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 302 Found"); // 회원가입 성공 후 리다이렉트
            softly.assertThat(output).contains("Location: /index.html");
        });
    }

    @Test
    @DisplayName("쿠키가 포함된 요청 처리")
    void requestWithCookies() {
        // given
        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=ABC123; theme=dark",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 200 OK");
            softly.assertThat(output).contains("Content-Type: text/html;charset=UTF-8");
        });
    }

    @Test
    @DisplayName("잘못된 HTTP 메서드도 처리 시도")
    void invalidHttpMethodStillProcessed() {
        // given
        final String request = String.join("\r\n",
                "INVALID /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when & then
        // 잘못된 HTTP 메서드도 예외가 발생하지 않고 처리를 시도해야 함
        processor.process(socket);
    }

    @Test
    @DisplayName("최소한의 헤더로도 요청 처리")
    void requestWithMinimalHeaders() {
        // given
        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        // 최소한의 헤더로도 기본적인 응답은 받을 수 있어야 함
        assertThat(output).contains("HTTP/1.1");
    }

    @Test
    @DisplayName("매우 긴 URL 요청")
    void requestWithVeryLongUrl() {
        // given
        final StringBuilder longPath = new StringBuilder("/");
        for (int i = 0; i < 100; i++) {
            longPath.append("very-long-path-segment-");
        }

        final String request = String.join("\r\n",
                "GET " + longPath + " HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        // 긴 URL도 적절히 처리되어야 함
        assertThat(output).contains("HTTP/1.1");
    }

    @Test
    @DisplayName("Content-Length가 0인 POST 요청")
    void postRequestWithZeroContentLength() {
        // given
        final String request = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 0",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertThat(output).contains("HTTP/1.1");
    }
}
