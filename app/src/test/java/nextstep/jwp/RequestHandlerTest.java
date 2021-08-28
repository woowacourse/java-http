package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(output).isEqualTo(expected);
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

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void loginWithCorrectQueryString() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html \r\n" +
                "\r\n" +
                "";
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void loginWithIncorrectQueryString() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=notpassword HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

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
    void loginWithCorrectRequestBody() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length + " ",
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
    }

    @Test
    void loginWithIncorrectRequestBody() {
        // given
        final String requestBody = "account=gugu&password=notpassword";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length + " ",
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
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
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
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void js() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*",
                "Connection: keep-alive ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/js;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;
        assertThat(output).isEqualTo(expected);
    }

    private String runRequestHandler(String httpRequest) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);
        requestHandler.run();
        return socket.output();
    }
}
