package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.controller.LoginRequestController;
import com.techcourse.controller.RootRequestController;
import com.techcourse.controller.SignupRequestController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.RequestHandlerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
        requestHandlerMapper.addController(new RootRequestController(), "/");
        final var processor = new Http11Processor(socket, requestHandlerMapper);

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
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5670 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("정적 리소스를 요청하면 정적 리소스를 응답한다.")
    @Test
    void staticResourceRequest() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);
        String fileName = getClass().getClassLoader().getResource("static/css/styles.css").getFile();
        String resourceContent = new String(Files.readAllBytes(new File(fileName).toPath()));

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: " + resourceContent.getBytes().length + " \r\n" +
                "\r\n" +
                resourceContent;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login 에 GET 요청을 보내면, login.html을 응답한다.")
    @Test
    void login_GET() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
        requestHandlerMapper.addController(new LoginRequestController(), "/login");
        Http11Processor processor = new Http11Processor(socket, requestHandlerMapper);

        processor.process(socket);
        String fileName = getClass().getClassLoader().getResource("static/login.html").getFile();
        String resourceContent = new String(Files.readAllBytes(new File(fileName).toPath()));

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceContent.getBytes().length + " \r\n" +
                "\r\n" +
                resourceContent;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login 에 세션ID와 함께 GET 요청을 보내면, index.html로 리다이렉트한다.")
    @Test
    void login_GET_WithSessionID() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=sssssss ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
        requestHandlerMapper.addController(new LoginRequestController(), "/login");
        Http11Processor processor = new Http11Processor(socket, requestHandlerMapper);

        processor.process(socket);
        String fileName = getClass().getClassLoader().getResource("static/index.html").getFile();
        String resourceContent = new String(Files.readAllBytes(new File(fileName).toPath()));

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceContent.getBytes().length + " \r\n" +
                "\r\n" +
                resourceContent;
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /index.html")
        );
    }

    @DisplayName("/login 에 POST 요청이 성공하면, index.html 리다이렉트와 세션ID을 응답한다.")
    @Test
    void login_POST_Success() throws IOException {
        String httpBody = "account=gugu&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + httpBody.getBytes().length,
                "",
                httpBody);

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
        requestHandlerMapper.addController(new LoginRequestController(), "/login");
        Http11Processor processor = new Http11Processor(socket, requestHandlerMapper);

        processor.process(socket);
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /index.html"),
                () -> assertThat(actual).contains("Set-Cookie: JSESSIONID=")
        );
    }

    @DisplayName("/login 에 POST 요청(로그인)이 실패하면, 401.html을 리다이렉트한다.")
    @Test
    void login_POST_Fail() throws IOException {
        String httpBody = "account=wasdasd&password=asdasd";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + httpBody.getBytes().length,
                "",
                httpBody);

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
        requestHandlerMapper.addController(new LoginRequestController(), "/login");
        Http11Processor processor = new Http11Processor(socket, requestHandlerMapper);

        processor.process(socket);
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /401.html")
        );
    }

    @DisplayName("/register 에 GET 요청을 보내면, register.html을 응답한다.")
    @Test
    void register_GET() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
        requestHandlerMapper.addController(new SignupRequestController(), "/register");
        Http11Processor processor = new Http11Processor(socket, requestHandlerMapper);

        processor.process(socket);
        String fileName = getClass().getClassLoader().getResource("static/register.html").getFile();
        String resourceContent = new String(Files.readAllBytes(new File(fileName).toPath()));

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceContent.getBytes().length + " \r\n" +
                "\r\n" +
                resourceContent;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register 에 POST 요청이 성공하면, index.html 리다이렉트와 세션ID을 응답한다.")
    @Test
    void register_POST_Success() throws IOException {
        String httpBody = "account=wiib&email=test@test&password=test";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + httpBody.getBytes().length,
                "",
                httpBody);

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
        requestHandlerMapper.addController(new SignupRequestController(), "/register");
        Http11Processor processor = new Http11Processor(socket, requestHandlerMapper);

        processor.process(socket);
        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /index.html"),
                () -> assertThat(actual).contains("Set-Cookie: JSESSIONID=")
        );
    }

    @DisplayName("없는 리소스에 대해 요청을 보내면 코드 404와 404.html를 응답한다.")
    @Test
    void noExists() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /ggggg HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);
        String fileName = getClass().getClassLoader().getResource("static/404.html").getFile();
        String resourceContent = new String(Files.readAllBytes(new File(fileName).toPath()));

        String expected = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceContent.getBytes().length + " \r\n" +
                "\r\n" +
                resourceContent;

        assertThat(socket.output()).isEqualTo(expected);
    }


}
