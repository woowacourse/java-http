package nextstep.jwp;

import static nextstep.jwp.TestFixture.runRequestHandler;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
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
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
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
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                "");

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
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
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                requestBody);

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
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
                "Cookie: JSESSIONID=" + UUID.randomUUID() + " ",
                "",
                requestBody);

        // when
        String output = runRequestHandler(httpRequest);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /401.html \r\n" +
                "\r\n" +
                "";
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void loginAgain() {
        // given
        final String requestBody = "account=gugu&password=password";
        final UUID uuid = UUID.randomUUID();
        final String firstRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "Cookie: JSESSIONID=" + uuid + " ",
                "",
                requestBody);
        runRequestHandler(firstRequest);
        final String secondRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + uuid + " ",
                "",
                "");

        // when
        String output = runRequestHandler(secondRequest);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "\r\n" +
                "";
        assertThat(output).isEqualTo(expected);
    }
}
