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

    @Test
    @DisplayName("GET /index.html로 요청을 할 경우, resources/static/index.html을 response로 응답한다.")
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("GET /login으로 요청을 할 경우, resources/static/login.html을 response로 응답한다.")
    void login() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("POST /login로 요청했는데, 로그인에 실패하면 401.html로 리다이렉트한다.")
    void login_queryString() throws IOException {
        // given
        String formData = "account=gugu&password=wrong";
        final String httpRequest = String.join("\r\n",
            "POST /login?account=gugu&password=wrong HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + formData.length(),
            "",
            formData);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("POST /login로 요청해서, 로그인 성공하면 응답 헤더에 http status code를 302로 반환한다.")
    void login_redirect() throws IOException {
        // given
        String formData = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + formData.length(),
            "",
            "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302";
        String expected2 = "Location: /index.html";
        assertThat(socket.output()).contains(expected);
        assertThat(socket.output()).contains(expected2);
    }

    @Test
    @DisplayName("GET /register로 요청을 할 경우, resources/static/register.html을 response로 응답한다.")
    void register() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("POST /register로 요청을 할 경우, 회원가입을 완료하면 index.html로 리다이렉트한다.")
    void register_formData() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 80",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302";
        String expected2 = "Location: /index.html";
        assertThat(socket.output()).contains(expected);
        assertThat(socket.output()).contains(expected2);
    }
}
