package nextstep.org.apache.coyote.http11;

import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.cookie.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void index() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        // when
        processor.process(socket);
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Set-Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        // then
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index_css() {
        // given
        final String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 200 OK ");
        assertThat(socket.output()).contains("Content-Type: text/css;charset=utf-8 ");
    }

    @Test
    void index_js() {
        // given
        final String request = String.join("\r\n",
                "GET scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 200 OK ");
        assertThat(socket.output()).contains("Content-Type: Application/javascript;charset=utf-8 ");
    }

    @Test
    void login_success_redirect_index() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        // when
        processor.process(socket);
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Redirected ",
                "Set-Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        // then
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_failed_redirect_401() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login?account=judy&password=j HTTP/1.1 ",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);
        final URL resource = getClass().getClassLoader().getResource("static/401.html");

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_not_saved_jsession() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);
        final URL resource = getClass().getClassLoader().getResource("static/login.html");

        // when
        processor.process(socket);
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Set-Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 3796 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_saved_jsession_redirect_index() throws IOException {
        // given
        final User judy = new User("judy", "judy", "judy@naver.com");
        final HttpCookie httpCookie = new HttpCookie();
        httpCookie.changeJSessionId("74262fcd-872c-4bcb-ace8-4bb003882818");
        SessionManager.add(judy, httpCookie);

        final String request = String.join("\r\n", "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        // when
        processor.process(socket);
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Redirected ",
                "Set-Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_post() throws IOException {
        // given
        final String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com ");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        // when
        processor.process(socket);
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Redirected ",
                "Set-Cookie: JSESSIONID=74262fcd-872c-4bcb-ace8-4bb003882818 ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
