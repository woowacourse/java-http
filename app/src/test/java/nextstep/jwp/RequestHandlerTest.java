package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.handler.RequestHandler;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @DisplayName("서버를 실행시켜서 브라우저로 서버에 접속하면 index.html 페이지를 보여준다.")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("http://localhost:8080/login으로 접속하면 로그인 페이지(login.html)를 보여준다.")
    @Test
    void login_get() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                content;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 성공하면 응답 헤더에 http status code를 302로 반환한다.")
    @Test
    void login_post() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "Location: /index.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                content;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 실패하면 응답 헤더에 http status code를 401로 반환한다.")
    @Test
    void login_post_fail() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "account=jinho&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 401 Unauthorized \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "Location: /401.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                content;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.")
    @Test
    void register_get() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                content;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용하고 완료하면 index.html로 리다이렉트한다.")
    @Test
    void register_post() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Content-Length: 56",
                "Content-Type: application/x-www-form-urlencoded",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "account=jinho&email=jh8579@gmail.com&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "Location: /index.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                content;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("클라이언트에서 요청하면 CSS 파일도 제공하도록 수정한다.")
    @Test
    void serve_css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "Content-Type: text/css \r\n" +
                "\r\n" +
                content;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("파일이 존재하지 않을 때 404 error page")
    @Test
    void file_not_exists() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles1.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "Location: /404.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                content;
        assertThat(socket.output()).isEqualTo(expected);
    }
}
