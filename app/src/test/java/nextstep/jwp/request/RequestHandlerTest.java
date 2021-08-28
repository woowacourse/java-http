package nextstep.jwp.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.MockSocket;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Test
    void run() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 5564 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
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

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 5564 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("User 객체가 저장되었는지 확인한다.")
    @Test
    void queryString_SaveUser_Success() {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /login.html?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");

        // when
        requestHandler.run();

        User findUser = InMemoryUserRepository.findByAccount("gugu");

        // then
        assertThat(findUser)
            .usingRecursiveComparison()
            .ignoringFields("email")
            .isEqualTo(user);
    }

    @DisplayName("여러 User 객체가 저장되었는지 확인한다.")
    @Test
    void queryString_saveUsers_Success() {
        // given
        final String httpRequest1 = String.join("\r\n",
            "GET /login.html?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        final String httpRequest2 = String.join("\r\n",
            "GET /login.html?account=dani&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket1 = new MockSocket(httpRequest1);
        final RequestHandler requestHandler1 = new RequestHandler(socket1);
        final MockSocket socket2 = new MockSocket(httpRequest2);
        final RequestHandler requestHandler2 = new RequestHandler(socket2);

        User user1 = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        User user2 = new User(2L, "dani", "password", null);

        // when
        requestHandler1.run();
        requestHandler2.run();

        User findUser1 = InMemoryUserRepository.findByAccount("gugu");
        User findUser2 = InMemoryUserRepository.findByAccount("dani");

        // then
        assertThat(findUser1)
            .usingRecursiveComparison()
            .ignoringFields("email")
            .isEqualTo(user1);
        assertThat(findUser2)
            .usingRecursiveComparison()
            .ignoringFields("email")
            .isEqualTo(user2);
    }
}
