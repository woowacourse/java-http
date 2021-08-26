package nextstep.jwp;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

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
            "GET /login.html HTTP/1.1 ",
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
    @DisplayName("GET /login?account=gugu&password=wrong로 요청했는데, 로그인에 실패하면 401.html로 리다이렉트한다.")
    void login_queryString() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /login?account=gugu&password=wrong HTTP/1.1 ",
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
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("GET /login?account=gugu&password=password로 요청해서, 로그인 성공하면 응답 헤더에 http status code를 302로 반환한다.")
    void login_redirect() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /login?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302";
        System.out.println(socket.output());
        assertThat(socket.output()).contains(expected);
    }
}
