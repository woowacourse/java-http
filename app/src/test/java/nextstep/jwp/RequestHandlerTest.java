package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
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

    @DisplayName("GET /index.html 요청시 파일 불러오기")
    @Test
    void index() throws IOException {
        // given
        final MockSocket socket = getMockSocket("/index.html");
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        assertThat(socket.output()).isEqualTo(getExpected(5564, resource));
    }

    @DisplayName("GET /login 요청시 login.html 불러오기")
    @Test
    void getLogin() throws IOException {
        // given
        final MockSocket socket = getMockSocket("/login.html");
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");

        assertThat(socket.output()).isEqualTo(getExpected(3797, resource));
    }

    @DisplayName("GET /register 요청시 register.html 불러오기")
    @Test
    void getRegister() throws IOException {
        // given
        final MockSocket socket = getMockSocket("/register.html");
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");

        assertThat(socket.output()).isEqualTo(getExpected(4319, resource));
    }

    private MockSocket getMockSocket(String url) {
        final String httpRequest = String.join("\r\n",
            "GET " + url + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        return new MockSocket(httpRequest);
    }

    private String getExpected(int length, URL resource) throws IOException {
        return "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: " + length + " \r\n" +
            "\r\n"+
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
