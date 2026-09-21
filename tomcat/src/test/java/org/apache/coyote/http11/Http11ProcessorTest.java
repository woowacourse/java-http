package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket("GET / HTTP/1.1\r\nCookie: JSESSIONID=existing\r\n\r\n");
        final var processor = new Http11Processor(socket, new SessionManager());

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
    void index() throws IOException, URISyntaxException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);

        assertThat(socket.output()).isEqualTo(expected);
    }
    @Test
    void issuesCookieOnlyWhenMissingForEachResponseType() {
        String[] requests = {
                "GET / HTTP/1.1",
                "GET /missing-file HTTP/1.1",
                "POST /login HTTP/1.1",
                "POST /register HTTP/1.1"
        };
        String[] statuses = {"200", "404", "302", "302"};
        for (int i = 0; i < requests.length; i++) {
            for (boolean hasCookie : new boolean[]{false, true}) {
                String body = "account=cookietest-" + i + "-" + hasCookie
                        + "&password=test&email=test@example.com";
                String request = requests[i] + "\r\nContent-Length: " + body.length() + "\r\n"
                        + (hasCookie ? "Cookie: JSESSIONID=existing\r\n" : "")
                        + "\r\n" + body;
                StubSocket socket = new StubSocket(request);
                new Http11Processor(socket, new SessionManager()).process(socket);
                String response = socket.output();
                String headers = response.substring(0, response.indexOf("\r\n\r\n"));

                assertThat(response).startsWith("HTTP/1.1 " + statuses[i]);
                if (hasCookie) {
                    assertThat(headers).doesNotContain("Set-Cookie:");
                } else {
                    String cookieHeader = headers.lines()
                            .filter(line -> line.startsWith("Set-Cookie: "))
                            .findFirst().orElseThrow();
                    assertThat(cookieHeader).startsWith("Set-Cookie: JSESSIONID=").endsWith("; Path=/");
                    String id = cookieHeader.substring("Set-Cookie: JSESSIONID=".length(),
                            cookieHeader.indexOf(";"));
                    java.util.UUID.fromString(id);
                }
                if (statuses[i].equals("302")) {
                    assertThat(headers).contains("Content-Length: 0");
                    assertThat(response.substring(response.indexOf("\r\n\r\n") + 4)).isEmpty();
                }
            }
        }
    }

    @Test
    void successfulLoginKeepsOnlyThatSessionLoggedIn() {
        String account = "user-" + UUID.randomUUID();
        String registration = "account=" + account + "&password=test&email=test@example.com";
        send(postRequest("/register", registration));

        String login = send(postRequest("/login", "account=" + account + "&password=test"));
        assertThat(login).startsWith("HTTP/1.1 302");
        assertThat(headers(login)).contains("Location: /index.html");

        String sessionCookie = headers(login).lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .findFirst().orElseThrow()
                .substring("Set-Cookie: ".length())
                .split(";", 2)[0];

        String sameSession = send("GET /login HTTP/1.1\r\nCookie: " + sessionCookie + "\r\n\r\n");
        assertThat(sameSession).startsWith("HTTP/1.1 302");
        assertThat(headers(sameSession)).contains("Location: /index.html");

        String otherSession = send("GET /login HTTP/1.1\r\nCookie: JSESSIONID=" + UUID.randomUUID() + "\r\n\r\n");
        assertThat(otherSession).startsWith("HTTP/1.1 200");
        assertThat(headers(otherSession)).doesNotContain("Location:");
    }

    @Test
    void failedLoginRedirectsToUnauthorizedPageWithoutLoggingIn() {
        String account = "user-" + UUID.randomUUID();
        send(postRequest("/register", "account=" + account + "&password=test&email=test@example.com"));

        String sessionCookie = "JSESSIONID=" + UUID.randomUUID();
        String failure = send(postRequest("/login", "account=" + account + "&password=wrong", sessionCookie));
        assertThat(failure).startsWith("HTTP/1.1 302");
        assertThat(headers(failure)).contains("Location: /401.html");

        String nextRequest = send("GET /login HTTP/1.1\r\nCookie: " + sessionCookie + "\r\n\r\n");
        assertThat(nextRequest).startsWith("HTTP/1.1 200");
        assertThat(headers(nextRequest)).doesNotContain("Location:");
    }

    @Test
    void duplicateRegistrationDoesNotReplaceExistingUser() {
        String account = "user-" + UUID.randomUUID();
        String registration = "account=" + account + "&password=original&email=test@example.com";
        assertThat(send(postRequest("/register", registration))).startsWith("HTTP/1.1 302");

        String duplicate = "account=" + account + "&password=replaced&email=other@example.com";
        assertThat(send(postRequest("/register", duplicate))).startsWith("HTTP/1.1 409");
        assertThat(InMemoryUserRepository.findByAccount(account).orElseThrow().checkPassword("original")).isTrue();
        assertThat(InMemoryUserRepository.findByAccount(account).orElseThrow().checkPassword("replaced")).isFalse();
    }

    private String send(String request) {
        StubSocket socket = new StubSocket(request);
        new Http11Processor(socket, new SessionManager()).process(socket);
        return socket.output();
    }

    private String postRequest(String path, String body) {
        return postRequest(path, body, null);
    }

    private String postRequest(String path, String body, String cookie) {
        return "POST " + path + " HTTP/1.1\r\nContent-Length: "
                + body.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                + (cookie == null ? "" : "Cookie: " + cookie + "\r\n")
                + "\r\n" + body;
    }

    private String headers(String response) {
        return response.substring(0, response.indexOf("\r\n\r\n"));
    }
}
