package nextstep.jwp.controller;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    void registerPage() throws IOException {
        // given
        final String httpRequest = "GET /register HTTP/1.1";

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expected = "HTTP/1.1 200 OK" + LINE_SEPARATOR +
                "Content-Length: 4319" + LINE_SEPARATOR +
                "Content-Type: text/html;charset=utf-8" + LINE_SEPARATOR +
                LINE_SEPARATOR +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register() {
        // given
        final String httpRequest = "POST /register HTTP/1.1" + LINE_SEPARATOR +
                "Content-Length: 55" + LINE_SEPARATOR +
                LINE_SEPARATOR +
                "account=kimkim&password=password&email=kimkim@woowa.com";

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        Optional<User> actual = InMemoryUserRepository.findByAccount("kimkim");
        assertThat(actual).isPresent();
        assertThat(actual.get()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new User("kimkim", "password", "kimkim@woowa.com"));
    }
}
