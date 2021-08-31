package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Stream;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestHandlerTest {

    @ParameterizedTest
    @DisplayName("GET /xxx.html 또는 /xxx 형태로 요청할 경우, resources/static/xxx.html을 response로 응답한다.")
    @MethodSource("generateData")
    void renderHtmlFile(String request, String path) throws IOException {
        // given
        final String httpRequest = request;

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource(path);
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected);
    }

    static Stream<Arguments> generateData() {
        return Stream.of(
            Arguments.of(
                String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    ""),
                "static/index.html"
            ),
            Arguments.of(
                String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    ""),
                "static/login.html"
            ),
            Arguments.of(
                String.join("\r\n",
                    "GET /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    ""),
                "static/register.html"
            )
        );
    }

    @Test
    @DisplayName("POST /login로 요청했는데, 로그인에 실패하면 401.html로 리다이렉트한다.")
    void login_queryString() throws IOException {
        // given
        String formData = "account=gugu&password=wrong";
        final String httpRequest = String.join("\r\n",
            "POST /login?account=gugu&password=wrong HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + formData.length(),
            "",
            formData);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("POST /login로 요청해서, 로그인 성공하면 응답 헤더에 http status code를 302로 반환한다.")
    void login_redirect() {
        // given
        HttpSessions.put(new HttpSession("656cef62-e3c4-40bc-a8df-94732920ed46"));
        String formData = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + formData.length(),
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
            "",
            "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302";
        String expected2 = "Location: /index.html";
        assertThat(socket.output()).contains(expected);
        assertThat(socket.output()).contains(expected2);
    }

    @Test
    @DisplayName("POST /register로 요청을 할 경우, 회원가입을 완료하면 index.html로 리다이렉트한다.")
    void register_formData() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 80",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302";
        String expected2 = "Location: /index.html";
        assertThat(socket.output()).contains(expected);
        assertThat(socket.output()).contains(expected2);
    }

    @Test
    @DisplayName("JSESSIONID가 없으면 Http Response Header에 Set-Cookie를 통해 JSESSIONID를 반환해준다.")
    void JSessionId() {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 80",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    @DisplayName("JSESSIONID가 있으면 Http Response Header에 Set-Cookie에 JSESSIONID가 포함되지 않는다.")
    void JSessionId_existing() {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 80",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).doesNotContain("JSESSIONID");
    }
}
