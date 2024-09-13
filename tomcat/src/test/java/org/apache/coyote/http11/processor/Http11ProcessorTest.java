package org.apache.coyote.http11.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private static final String ROOT_LOCATION = "http://localhost:8080/\r\n";

    @DisplayName("uri가 / 일경우 index.html을 반환한다.")
    @Test
    void response_index_html_When_request_default() throws IOException {
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
        String expectedContent = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedContent);
    }

    @DisplayName("/login Get요청시 login페이지를 반환한다.")
    @Test
    void response_login_html_When_request_get_login() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(expectedBody);
    }

    @DisplayName("/login Post요청시, 성공 -> root location 리다이렉션")
    @Test
    void response_redirect_default_uri_When_request_post_login_success() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + "account=gugu&password=password".length(),
                "",
                "account=gugu&password=password");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(ROOT_LOCATION);
    }

    @DisplayName("/login Post요청시, 실패 -> 401페이지 반환")
    @Test
    void response_401_html_When_request_post_login_fail() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + "account=gugu&password=password".length(),
                "",
                "account=gugu&password=failPassword");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(expectedBody);
    }


    @DisplayName("/register Post요청시, 성공-> index페이지 반환")
    @Test
    void response_redirect_default_uri_When_request_post_register() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + "account=gugu&password=password".length(),
                "",
                "account=dodo&email=dodo@a.com&password=password");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(ROOT_LOCATION);
    }

    @DisplayName("/register Get요청시, 성공-> index페이지 반환")
    @Test
    void response_redirect_default_uri_When_request_get_register() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(expectedBody);
    }

    @DisplayName("/login GET 요청 시, 이미 로그인해 있다면 루트로 다이렉트")
    @Test
    void direct_root_path_When_get_login_with_already_login() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + "account=gugu&password=password".length(),
                "",
                "account=gugu&password=password");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        // given
        String jSessionId = InMemoryUserRepository.findByAccount("gugu").get().getId().toString();
        String httpGetLoginRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + jSessionId,
                "",
                "");
        StubSocket socket2 = new StubSocket(httpGetLoginRequest);
        Http11Processor processor2 = new Http11Processor(socket2);

        // when
        processor2.process(socket2);

        //then
        assertThat(socket2.output()).contains(ROOT_LOCATION);
    }

    @DisplayName("css 자원 Get 요청 시 css content로 반환가능하다.")
    @Test
    void response_css_content_When_get_css_resources() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        processor.process(socket);

        String response = socket.output();
        // then
        assertAll(
                () -> assertThat(response).contains("text/css"),
                () -> assertThat(response).contains(expectedBody)
        );


    }

}
