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
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestHandlerTest {
    private final String jSessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
    private final String cookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=" + jSessionId;
    private final String formData = "account=gugu&password=password&email=hkkang%40woowahan.com";


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
                createRequestOfGet("/index.html"),
                "static/index.html"
            ),
            Arguments.of(
                createRequestOfGet("/login"),
                "static/login.html"
            ),
            Arguments.of(
                createRequestOfGet("/register"),
                "static/register.html"
            )
        );
    }

    @Test
    @DisplayName("POST /login로 요청했는데, 로그인에 실패하면 401.html로 리다이렉트한다.")
    void login_queryString() throws IOException {
        // given
        String formData = "account=gugu&password=wrong";

        String httpRequest = createRequestOfPostWithFormData("/login?account=gugu&password=wrong", formData);

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
        HttpSessions.put(new HttpSession(jSessionId));
        String formData = "account=gugu&password=password";

        String httpRequest = createRequestOfPostWithFormDataAndCookie("/login", formData, "JESSINOID=" + jSessionId);

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
    void register_formData() {
        // given
        String httpRequest = createRequestOfPostWithFormData("/register", formData);

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
        String httpRequest = createRequestOfPostWithFormData("/register", formData);

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
        String httpRequest = createRequestOfPostWithFormDataAndCookie("/register", formData, cookie);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).doesNotContain("JSESSIONID");
    }

    @Test
    @DisplayName("이미 로그인이 된 경우에 GET /login 요청 시 index.html을 띄워준다.")
    void already_login_redirect() throws IOException {
        // given
        String httpRequest = createRequestOfGetWithCookie("/login", cookie);

        HttpSession httpSession = new HttpSession(jSessionId);
        httpSession.setAttribute("user", new User("abcd", "password", "email"));
        HttpSessions.put(httpSession);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected);
    }

    private static String createRequestOfGet(String url) {
        return String.join("\r\n",
            "GET " + url + " HTTP/1.1 ",
            "",
            "");
    }

    private static String createRequestOfGetWithCookie(String url, String cookie) {
        return String.join("\r\n",
            "GET " + url + " HTTP/1.1 ",
            "Cookie: " + cookie,
            "",
            "");
    }

    private static String createRequestOfPostWithFormData(String url, String formData) {
        return String.join("\r\n",
            "POST " + url + " HTTP/1.1 ",
            "Content-Length: " + formData.length(),
            "",
            formData);
    }

    private static String createRequestOfPostWithFormDataAndCookie(String url, String formData, String cookie) {
        return String.join("\r\n",
            "POST " + url + " HTTP/1.1 ",
            "Content-Length: " + formData.length(),
            "Cookie: " + cookie,
            "",
            formData);
    }
}
