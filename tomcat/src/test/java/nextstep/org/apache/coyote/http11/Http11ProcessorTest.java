package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.adaptor.ControllerMapping;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private static final ControllerMapping CONTROLLER_ADAPTOR = new ControllerMapping();

    @Test
    void process() {
        // given
        var socket = new StubSocket();
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

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
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        var socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void getCss() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("@charset \"UTF-8\";");
    }

    @Test
    void getLoginPage() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html,*/*;q=0.1");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void loginSuccess() {
        // given
        String body = "account=gugu&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("302 Found");
        assertThat(socket.output()).contains("Location: index.html");
    }

    @Test
    void getRegisterPage() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html,*/*;q=0.1");

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("<title>회원가입</title>");
    }

    @Test
    void registerSuccess() {
        // given
        String body = "account=doggy&email=doggy@gmail.com&password=doggy1";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("302 Found");
        assertThat(socket.output()).contains("Location: index.html");
    }

    @Test
    void containSetCookieWhenLoginSuccess() {
        // given
        String body = "account=gugu&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void redirectBadRequestPageWhenNotExistUser() {
        // given
        String body = "account=brown&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("Location: 401.html");
    }

    @Test
    void redirectBadRequestPageWhenInvalidPassword() {
        // given
        String body = "account=gugu&password=password1234";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "",
                body);

        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("302 Found");
        assertThat(socket.output()).contains("Location: 401.html");
    }

    @Test
    void automaticRedirectWhenValidSessionId() {
        // given
        // POST login - success
        String body = "account=gugu&password=password";
        String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "",
                body);
        var loginSocket = new StubSocket(loginRequest);
        var loginProcessor = new Http11Processor(loginSocket, CONTROLLER_ADAPTOR);
        loginProcessor.process(loginSocket);
        String loginResponse = loginSocket.output();
        String setCookie = loginResponse.split("\r\n")[2];
        String session = setCookie.split(": ")[1];
        String sessionId = session.split("=")[1];

        // GET LoginPage
        String request = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html,*/*;q=0.1",
                "Cookie: JSESSIONID=" + sessionId);
        var socket = new StubSocket(request);
        var processor = new Http11Processor(socket, CONTROLLER_ADAPTOR);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("302 Found");
        assertThat(socket.output()).contains("Location: index.html");
    }
}
