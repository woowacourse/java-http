package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.techcourse.db.InMemoryUserRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
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
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Nested
    @DisplayName("로그인 관련기능")
    class Login {
        @Test
        void 확장자가_붙지않은_login_요청이라도_로그인_페이지를_응답한다() {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains("<title>로그인</title>");
        }

        @Test
        void 비밀번호가_일치하지_않으면_401로_리다이렉트_한다() {
            // given
            final String body = "account=gugu&password=wrong-password";
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body,
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                    .contains("HTTP/1.1 302 FOUND")
                    .contains("Location: /401.html");
        }

        @Test
        void 로그인에_성공하면_index_html로_리다이렉트_한다() {
            // given
            final String body = "account=gugu&password=password";
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body,
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                    .contains("HTTP/1.1 302 FOUND")
                    .contains("Location: /index.html");
        }
    }


    @Test
    @DisplayName("POST 요청 시 Content-Length만큼만 읽어 블로킹 없이 성공하고 유저를 등록해야 한다")
    void processPostRequestWithoutBlocking() {
        // 1. 회원가입 POST 요청 패킷 구성 (끝에 줄바꿈이 없음)
        String mockPostRequest =
                "POST /register HTTP/1.1\r\n" +
                        "Host: localhost:8080\r\n" +
                        "Content-Length: 49\r\n" +
                        "\r\n" + // 헤더 끝 구분 빈 줄
                        "account=testuser&password=password123&email=a@a.com"; // 💡 줄바꿈 없는 순수 바디

        // 2. 소켓의 입출력을 메모리 스트림으로 대체하는 가상 Mock 소켓 생성
        ByteArrayInputStream inputStream = new ByteArrayInputStream(mockPostRequest.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Socket mockSocket = new Socket() {
            @Override
            public InputStream getInputStream() { return inputStream; }
            @Override
            public OutputStream getOutputStream() { return outputStream; }
        };

        // 3. Http11Processor 생성
        Http11Processor processor = new Http11Processor(mockSocket);

        // 4. 타임아웃 검증 (블로킹이 걸린다면 1초 뒤 강제 종료되며 테스트 실패)
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            processor.process(mockSocket);
        }, "Http11Processor가 readLine() 블로킹에 빠져 1초 동안 응답하지 못했습니다.");

        // 5. 결과 검증 (정상 처리 되었다면 redirect 응답과 데이터 저장이 확인되어야 함)
        String responseText = outputStream.toString(StandardCharsets.UTF_8);

        // 회원가입 성공 후 /index.html로 리다이렉트(302 FOUND) 되었는지 확인
        assertTrue(responseText.contains("HTTP/1.1 302 Found") || responseText.contains("302"));
        assertTrue(responseText.contains("Location: /index.html"));

        // 메모리 DB에 유저가 정상적으로 저장되었는지 검증
        boolean isSaved = InMemoryUserRepository.findByAccount("testuser").isPresent();
        assertTrue(isSaved, "회원가입 요청은 성공했으나 데이터베이스에 유저가 저장되지 않았습니다.");
    }
}
