package nextstep.jwp;

import nextstep.jwp.mvc.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @DisplayName("/index path로 index.html 리소스에 대한 응답을 한다.")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/index.html path로 index.html 리소스에 대한 응답을 한다.")
    @Test
    void indexDotHTML() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login path로 GET 요청을 보내서 login.html 리소스에 대한 응답을 한다.")
    @Test
    void loginPage() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3797 \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login path로 POST 요청을 보내서 로그인 유저 정보가 유효하면 /index.html 로 리다이렉트 한다.")
    @Test
    void loginPost() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login path로 POST 요청을 보내서 로그인 유저 정보가 유효하지 않으면 /401.html 로 리다이렉트 한다.")
    @Test
    void loginWithInvalidUser() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=1234");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /401.html \r\n" +
                "\r\n";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register path로 GET 요청을 보내서 register.html 리소스에 대한 응답을 한다.")
    @Test
    void registerPage() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 4319 \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register path로 POST 요청을 보내서 로그인 유저 정보가 유효하면 /index 로 리다이렉트 한다.")
    @Test
    void register() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register path로 POST 요청을 보내서 회원가입에 실패하면 /409.html 로 리다이렉트 한다.")
    @Test
    void registerWithDuplicateUser() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 48 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=1234&email=gugu%40test.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /409.html \r\n" +
                "\r\n";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void queryStringExtract() {
        String requestPathWithQueryString = "/index.html?name=gugu&password=1234";

        int index2 = requestPathWithQueryString.indexOf('?');

        System.out.println(index2);

        String requestPath2 = requestPathWithQueryString.substring(0, index2);
        String requestQueryString2 = requestPathWithQueryString.substring(index2 + 1);
        System.out.println(requestPath2);
        System.out.println(requestQueryString2);

        Map<String, String> queryStrings = Stream.of(requestQueryString2.split("&")).map(queryString -> queryString.split("="))
                .collect(Collectors.toMap(queryString -> queryString[0], queryString -> queryString[1]));
        queryStrings.entrySet().forEach(entry -> System.out.println(entry.getKey() + ", " + entry.getValue()));
    }
}
