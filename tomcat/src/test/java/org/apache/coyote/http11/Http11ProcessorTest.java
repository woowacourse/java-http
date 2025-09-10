package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5670 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginSuccess() {
        // given
        String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found";
        assertThat(socket.output()).contains(expected);
    }

    @Test
    void loginFail() {
        // given
        String body = "account=gugu&password=pass";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 401 Unauthorized";
        assertThat(socket.output()).contains(expected);
    }

    @Test
    void registerSuccess() {
        // given
        String body = "account=qqq&password=qqq&email=qq@test.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 200 OK ";
        assertThat(socket.output()).contains(expected);
    }

    @Test
    void registerFail() {
        // given
        InMemoryUserRepository.save(new User("gugu", "password", "gugu@test.com"));
        String body = "account=gugu&password=password&email=gugu@test.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 400 Bad Request ";
        assertThat(socket.output()).contains(expected);
    }

    @Test
    void cookie() {
        // given
        String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).matches("(?s).*Set-Cookie: JSESSIONID=([a-z0-9\\-]+).*");
    }

    @Test
    void loginSession() throws IOException {
        String loginBody = "account=gugu&password=password";
        final String loginPostRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + loginBody.length(),
                "",
                loginBody);

        final var postSocket = new StubSocket(loginPostRequest);
        final Http11Processor postProcessor = new Http11Processor(postSocket);

        postProcessor.process(postSocket);
        String loginResponse = postSocket.output();
        String jsessionid = extractJsessionid(loginResponse);
        final String loginGetRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Cookie: JSESSIONID=" + jsessionid,
                "",
                "");

        final var getSocket = new StubSocket(loginGetRequest);
        final Http11Processor getProcessor = new Http11Processor(getSocket);

        getProcessor.process(getSocket);

        assertThat(getSocket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    private String extractJsessionid(String response) {
        String[] lines = response.split("\r\n");
        for (String line : lines) {
            if (line.startsWith("Set-Cookie: JSESSIONID=")) {
                String cookieValue = line.substring("Set-Cookie: JSESSIONID=".length());
                int semicolonIndex = cookieValue.indexOf(";");
                if (semicolonIndex != -1) {
                    return cookieValue.substring(0, semicolonIndex);
                }
                return cookieValue;
            }
        }
        return null;
    }
}
