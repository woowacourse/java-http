package nextstep.jwp;

import nextstep.jwp.ui.request.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    @DisplayName("/ 로 요청 시 Hello world를 응답 body로 보낸다.")
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /index.html 요청 시 index.html 페이지로 이동한다.")
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
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5670 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("등록되지 않은 url 요청 시 404.html로 redirect 한다.")
    void notExistUrl() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /oz HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /404.html";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /404.html 요청 시 404.html 페이지로 이동한다.")
    void moveNotFoundPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /404.html HTTP/1.1 ",
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
        String expected = "HTTP/1.1 404 NOT_FOUND \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 2477 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /401.html 요청 시 401.html 페이지로 이동한다.")
    void moveUnauthorizedPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /401.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expected = "HTTP/1.1 401 UNAUTHORIZED \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 2478 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /500.html 요청 시 500.html 페이지로 이동한다.")
    void moveInternalServerErrorPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /500.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/500.html");
        String expected = "HTTP/1.1 500 INTERNAL_SERVER_ERROR \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 2408 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }


    @Test
    @DisplayName("GET /login 요청 시 로그인 페이지로 이동한다.")
    void moveLoginPage() throws IOException {
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
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3863 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /login 요청 시 가입된 account와 password면 index.html로 redirect 된다.")
    void loginSuccess() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html";
        assertThat(socket.output()).isEqualTo(expected);
    }


    @Test
    @DisplayName("POST /login 요청 시 존재하지 않는 account면 401.html로 redirect 된다.")
    void loginFailWhenNotExistAccount() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=oz&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /401.html";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /login 요청 시 password가 일치하지 않으면 401.html로 redirect 된다.")
    void loginFailWhenNotExistPassword() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=123");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /401.html";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /register 요청 시 회원가입 페이지로 이동한다.")
    void getRegisterPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
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
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 4391 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /register 요청 시 회원가입이 성공하면 index.html로 redirect 된다.")
    void registerSuccess() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 23",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=oz&password=123");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html";
        assertThat(socket.output()).isEqualTo(expected);

    }

    @Test
    @DisplayName("POST /register 요청 시 회원가입이 실패하면 500.html로 redirect 된다.")
    void registerFail() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 23",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=123");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /500.html";
        assertThat(socket.output()).isEqualTo(expected);
    }
}
