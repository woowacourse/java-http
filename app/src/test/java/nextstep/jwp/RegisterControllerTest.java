package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.domain.User;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void registerWithCorrectInfo() {
        // given
        final String requestBody = "account=test&email=test@email.com&password=test";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                requestBody);

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html \r\n" +
                "\r\n" +
                "";
        assertThat(output).isEqualTo(expected);

        Optional<User> user = InMemoryUserRepository.findByAccount("test");
        assertThat(user).isPresent();
        assertThat(user.get().checkPassword("test")).isTrue();
        assertThat(user.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void registerWithDuplicateAccount() {
        // given
        final String requestBody = "account=gugu&email=test2@email.com&password=test";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                requestBody);

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /401.html \r\n" +
                "\r\n" +
                "";
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void registerWithDuplicateEmail() {
        // given
        final String requestBody = "account=test3&email=hkkang@woowahan.com&password=test";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                requestBody);

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /401.html \r\n" +
                "\r\n" +
                "";
        assertThat(output).isEqualTo(expected);
    }

    private String runRequestHandler(String httpRequest) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, requestMapping);
        requestHandler.run();
        return socket.output();
    }
}
