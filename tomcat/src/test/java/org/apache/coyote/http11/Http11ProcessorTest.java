package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        final var httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=existing-session ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void resourceNotFound() {
        final var httpRequest = String.join("\r\n",
                "GET /missing.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final var expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 13 ",
                "",
                "404 Not Found");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void staticResourceIsSentWithoutTextConversion() throws IOException {
        final var httpRequest = String.join("\r\n",
                "GET /assets/img/error-404-monochrome.svg HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);
        final var resource = getClass().getClassLoader()
                .getResource("static/assets/img/error-404-monochrome.svg");
        final var body = Files.readAllBytes(new File(resource.getFile()).toPath());

        processor.process(socket);

        final var responseHeader = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.length + " ",
                "",
                "");
        final var expectedOutput = new ByteArrayOutputStream();
        expectedOutput.writeBytes(responseHeader.getBytes(StandardCharsets.UTF_8));
        expectedOutput.writeBytes(body);

        assertThat(socket.outputBytes()).isEqualTo(expectedOutput.toByteArray());
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session ",
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
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        final var httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final var resource = getClass()
                .getClassLoader()
                .getResource("static/css/styles.css");
        final var body = Files.readString(
                new File(resource.getFile()).toPath(),
                StandardCharsets.UTF_8
        );

        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void registerPageIsServedWithGet() throws IOException {
        final var httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final var resource = getClass().getClassLoader().getResource("static/register.html");
        final var body = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void registerWithPostSavesUserAndRedirectsToIndex() {
        final var account = "new-user";
        final var requestBody = "account=" + account + "&password=password&email=new-user%40woowahan.com";
        final var socket = new StubSocket(postRequest("/register", requestBody));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final var expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
        assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
    }

    @Test
    void registerWithPostReadsBodyDeliveredInMultipleParts() {
        final var account = "split-request-user";
        final var requestBody = "account=" + account + "&password=password&email=split-request-user%40woowahan.com";
        final var socket = new StubSocket(postRequest("/register", requestBody), 1);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output()).contains("Location: /index.html");
        assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
    }

    @Test
    void loggedInUserIsRedirectedToIndexWhenAccessingLoginPage() {
        final var requestBody = "account=gugu&password=password";
        final var loginSocket = new StubSocket(postRequestWithoutSession("/login", requestBody));
        final var loginProcessor = new Http11Processor(loginSocket);

        loginProcessor.process(loginSocket);

        assertThat(loginSocket.output()).contains("Location: /index.html");
        final var sessionId = sessionIdFrom(loginSocket.output());
        final var loginPageRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=" + sessionId + " ",
                "",
                "");
        final var loginPageSocket = new StubSocket(loginPageRequest);
        final var loginPageProcessor = new Http11Processor(loginPageSocket);

        loginPageProcessor.process(loginPageSocket);

        final var expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                "");

        assertThat(loginPageSocket.output()).isEqualTo(expected);
    }

    @Test
    void loginFailureRedirectsToUnauthorizedPage() {
        final var requestBody = "account=gugu&password=wrong-password";
        final var socket = new StubSocket(postRequest("/login", requestBody));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final var expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /401.html",
                "Content-Length: 0",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void responseSetsSessionCookieWhenRequestDoesNotHaveJSessionId() {
        final var httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    void responseDoesNotSetSessionCookieWhenRequestAlreadyHasSessionId() {
        final var httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; JSESSIONID=existing-session ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output()).doesNotContain("Set-Cookie");
    }

    private String postRequest(final String path, final String requestBody) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session ",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                requestBody);
    }

    private String postRequestWithoutSession(final String path, final String requestBody) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                requestBody);
    }

    private String sessionIdFrom(final String response) {
        final var matcher = Pattern.compile("Set-Cookie: JSESSIONID=([^\\r\\n]+)")
                .matcher(response);

        assertThat(matcher.find()).isTrue();

        return matcher.group(1);
    }

}
