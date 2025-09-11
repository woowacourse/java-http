package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.processor.Http11Processor;
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
        String actualOutput = socket.output();

        assertThat(actualOutput).contains("HTTP/1.1 200 OK ");
        assertThat(actualOutput).contains("Hello world!");

        assertThat(actualOutput).contains("Content-Type: text/html;charset=utf-8 ");
        assertThat(actualOutput).contains("Content-Length: 12 ");
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

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String actualOutput = socket.output();
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        // 파일의 실제 바이트와 문자열 내용을 모두 준비.
        final byte[] fileContentBytes = Files.readAllBytes(new File(resource.getFile()).toPath());
        final String fileContent = new String(fileContentBytes);

        final String[] responseParts = actualOutput.split("\r\n\r\n", 2);
        final String headers = responseParts[0];
        final String body = responseParts[1];

        assertThat(headers).startsWith("HTTP/1.1 200 OK");

        // 헤더들이 응답에 포함되었는지 순서에 관계없이 확인합니다.
        assertThat(headers).contains("Content-Type: text/html;charset=utf-8");
        assertThat(headers).contains("Content-Length: " + fileContentBytes.length);

        // 바디 내용이 파일의 실제 내용과 일치하는지 확인합니다.
        assertThat(body).isEqualTo(fileContent);
    }

    @Test
    void getLoginPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String actualOutput = socket.output();
        final URL resource = getClass().getClassLoader().getResource("static/login.html");

        final byte[] fileContentBytes = Files.readAllBytes(new File(resource.getFile()).toPath());
        final String fileContent = new String(fileContentBytes);

        final String[] responseParts = actualOutput.split("\r\n\r\n", 2);
        final String headers = responseParts[0];
        final String body = responseParts[1];

        assertThat(headers).startsWith("HTTP/1.1 200 OK");
        assertThat(body).isEqualTo(fileContent);
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
