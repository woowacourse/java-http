package nextstep.org.apache.coyote.http11;

import nextstep.jwp.model.User;
import org.apache.cookie.Cookie;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import support.StubSocket;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

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
        var expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 13 ",
                "",
                "404 Not Found");

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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void CSS_파일을_호출한다() {
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
    void scripts_자바스크립트_파일을_호출한다() {
        // given
        final String request = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: Application/javascript,*/*;q=0.1 ",
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
    void assets_자바스크립트_파일을_호출한다() {
        // given
        final String request = String.join("\r\n",
                "GET /assets/chart-area.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: Application/javascript,*/*;q=0.1 ",
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
    void 로그인_성공시_302_응답후_redirect() throws IOException {
        // given
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 31",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&some=some");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        var httpResponse = "HTTP/1.1 302 Found ";
        var location = "Location: /index.html ";

        assertThat(socket.output()).contains(httpResponse);
        assertThat(socket.output()).contains(location);
    }

    @Test
    void 로그인_미성공시_401_redirect() throws IOException {
        // given
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 31",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=guga&password=password&some=some");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /401.html");

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void 세션_존재시_인덱스_페이지() throws IOException {
        // given
        final User user = new User("user", "password", "user@naver.com");
        Cookie cookie = Cookie.from("JSESSIONID=id");
        Map<String, Object> value = new HashMap<>();
        value.put("user", user);
        Session session = new Session("id", value);
        SessionManager.add(session);

        final String request = String.join("\r\n", "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=id",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        System.out.println("socket = " + socket.output());
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "",
                "null");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 세션_미존재시_인덱스_페이지() throws IOException {
        // given

        final String request = String.join("\r\n", "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        System.out.println("socket = " + socket.output());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3797 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));


        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 사용자_등록() throws IOException {
        // given
        final String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 31",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=hama&password=password&email=hama@naver.com");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        var httpResponse = "HTTP/1.1 302 Found ";
        var location = "Location: /index.html ";

        assertThat(socket.output()).contains(httpResponse);
        assertThat(socket.output()).contains(location);
    }
}
