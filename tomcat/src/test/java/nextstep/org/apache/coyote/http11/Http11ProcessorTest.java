package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;
import support.StubSocket;

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
        File file = new File(resource.getFile());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
//                "Content-Length: 5564 \r\n" +
                String.format("Content-Length: %d \r\n", file.length()) +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        File file = new File(resource.getFile());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                String.format("Content-Length: %d \r\n", file.length()) +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        File file = new File(resource.getFile());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                String.format("Content-Length: %d \r\n", file.length()) +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        File file = new File(resource.getFile());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                String.format("Content-Length: %d \r\n", file.length()) +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginSuccessTest() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                String.format("Content-Length: %d ", requestBody.getBytes().length),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "Set-Cookie: ";

        System.out.println(socket.output());

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void loginFailTest() {
        // given
        final String requestBody = "account=gugu&password=wrongPassword";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                String.format("Content-Length: %d ", requestBody.getBytes().length),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /401.html \r\n" +
                "";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void registerSuccessTest() {
        // given
        final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                String.format("Content-Length: %d ", requestBody.getBytes().length),
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void sessionTest() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                String.format("Content-Length: %d ", requestBody.getBytes().length),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        SessionManager sessionManager = new SessionManager();
        String output = socket.output();

        // Todo : httpHeaderParser 구현해서 분리하기
        Map<String, String> requestHeaders = new HashMap<>();
        List<String> splited = Arrays.asList(output.split("\r\n"));
        List<String> headers = splited.subList(1, splited.size());
        for (String line : headers) {
            String[] splitedLine = line.split(": ");
            requestHeaders.put(splitedLine[0], splitedLine[1].strip());
        }

        Cookies cookies = new Cookies();
        cookies.parseCookieHeaders(requestHeaders.get("Set-Cookie"));
        String jSessionId = cookies.get("JSESSIONID");
        User user = (User) sessionManager.findSession(jSessionId).getAttribute("gugu");

        assertThat(user).isNotNull();
    }

    @Test
    void loginRedirectionWithSessionTest() {
        // given
        String jSessionId = loginAndGetSessionId();

        final String httpGetRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                String.format("Cookie: JSESSIONID=%s", jSessionId),
                "",
                "");

        // when
        final var socket = new StubSocket(httpGetRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void wrongPathReturnNotFoundPageTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /nofile.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        File file = new File(resource.getFile());
        var expected = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                String.format("Content-Length: %d \r\n", file.length()) +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String loginAndGetSessionId() {
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                String.format("Content-Length: %d ", requestBody.getBytes().length),
                "",
                requestBody);

        var preSocket = new StubSocket(httpRequest);
        Http11Processor preProcessor = new Http11Processor(preSocket);

        preProcessor.process(preSocket);
        String preOutput = preSocket.output();

        // Todo : httpHeaderParser 구현해서 분리하기
        Map<String, String> requestHeaders = new HashMap<>();
        List<String> splited = Arrays.asList(preOutput.split("\r\n"));
        List<String> headers = splited.subList(1, splited.size());
        for (String line : headers) {
            String[] splitedLine = line.split(": ");
            requestHeaders.put(splitedLine[0], splitedLine[1].strip());
        }

        Cookies cookies = new Cookies();
        cookies.parseCookieHeaders(requestHeaders.get("Set-Cookie"));
        return cookies.get("JSESSIONID");
    }
}
