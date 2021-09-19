package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.http.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @DisplayName("인덱스 페이지 응답 성공")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=123",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + file.getBytes().length + " ",
                "",
                file);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("인덱스 페이지 응답 실패 - http method 오류")
    @Test
    void index_fail() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=123",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/405.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 405 Method Not Allowed ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + file.getBytes().length + " ",
                "",
                file);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 페이지 응답 성공")
    @Test
    void login() throws IOException {
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
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + file.getBytes().length + " ",
                "",
                file);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 성공 - 인덱스 페이지로 리다이렉트")
    @Test
    void login_success() {
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=123",
                "Content-Length: " + requestBody.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 실패 - 비밀번호 오류")
    @Test
    void login_fail() throws IOException {
        final String requestBody = "account=gugu&password=wrongpassword";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=123",
                "Content-Length: " + requestBody.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + file.getBytes().length + " ",
                "",
                file);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입 페이지 응답 성공")
    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=123",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + file.getBytes().length + " ",
                "",
                file);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입 성공")
    @Test
    void register_success() {
        final String requestBody = "account=solong&email=email@email.com&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=123",
                "Content-Length: " + requestBody.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입 실패 - 이미 가입한 유저")
    @Test
    void register_fail() throws IOException {
        final String requestBody = "account=gugu&email=email@email.com&password=wrongpassword";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=123",
                "Content-Length: " + requestBody.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 400 Bad Request ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + file.getBytes().length + " ",
                "",
                file);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("페이지 로드 실패 - 없는 페이지로 접근")
    @Test
    void notFound() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /wrong HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=123",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + file.getBytes().length + " ",
                "",
                file);
        assertThat(socket.output()).isEqualTo(expected);
    }
}
