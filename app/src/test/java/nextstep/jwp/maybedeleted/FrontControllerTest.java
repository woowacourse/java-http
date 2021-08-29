package nextstep.jwp.maybedeleted;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.FrontController;
import nextstep.jwp.MockSocket;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.user.UserNotFoundException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @Test
    void run() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final FrontController frontController = new FrontController(socket);

        // when
        frontController.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Length: 5564 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
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
        final FrontController frontController = new FrontController(socket);

        // when
        frontController.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Length: 5564 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
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
        final FrontController frontController = new FrontController(socket);

        User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");

        // when
        frontController.run();

        User findUser = InMemoryUserRepository.findByAccount("gugu")
            .orElseThrow(UserNotFoundException::new);

        // then
        assertThat(findUser)
            .usingRecursiveComparison()
            .ignoringFields("id", "email")
            .isEqualTo(user);
    }
}
