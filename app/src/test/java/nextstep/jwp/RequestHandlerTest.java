package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.web.RequestHandler;
import nextstep.jwp.web.http.request.HttpRequestFactory;
import nextstep.jwp.web.session.HttpSession;
import nextstep.jwp.web.session.HttpSessions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class RequestHandlerTest {

    private static MockedStatic<HttpSessions> mockHttpSessions;

    @BeforeAll
    static void beforeAll() {
        mockHttpSessions = mockStatic(HttpSessions.class);
    }

    @AfterAll
    static void afterAll() {
        mockHttpSessions.close();
    }

    @DisplayName("/로 접근시 Hello world! 가 출력된다.")
    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
            "HTTP/1.1 200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: 12",
            "",
            "Hello world!");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/index.html GET 요청 성공")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = HttpRequestFactory.getIndexHtml();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String path = "static/index.html";
        final String responseBody = readResource(path);
        final int contentLength = responseBody.getBytes(StandardCharsets.UTF_8).length;

        String expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: " + contentLength + "\r\n" +
            "\r\n" +
            responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login GET 최초 요청 성공 - 로그인 화면")
    @Test
    void getLogin() throws IOException {
        // given
        final String httpRequest = HttpRequestFactory.getFirstLogin();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        String sessionId = "sessionId";
        when(HttpSessions.getSession()).thenReturn(new HttpSession(sessionId));

        // when
        requestHandler.run();

        // then
        String path = "static/login.html";
        final String responseBody = readResource(path);
        final int contentLength = responseBody.getBytes(StandardCharsets.UTF_8).length;

        String expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: " + contentLength + "\r\n" +
            "\r\n" +
            responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("이미 로그인한 상태에서 /login GET 요청 성공 - index.html로 리다이렉트")
    @Test
    void geLogin_AlreadyLogin_redirect() {
        // given
        final String httpRequest = HttpRequestFactory.getAlreadyLogin();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        String sessionId = "sessionId";
        User user = new User(1L, "user", "password", "email@email");
        InMemoryUserRepository.save(user);

        HttpSession session = new HttpSession(sessionId);
        session.setAttribute("user", user);

        when(HttpSessions.getSession(anyString())).thenReturn(session);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found\r\n" +
            "Location: /index.html\r\n" +
            "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login POST 요청 성공")
    @Test
    void postLogin() {
        // given
        final String httpRequest = HttpRequestFactory.postLoginWithRequestBody();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        String sessionId = "sessionId";
        when(HttpSessions.getSession()).thenReturn(new HttpSession(sessionId));

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found\r\n" +
            "Location: /index.html\r\n" +
            "Set-Cookie: JSESSIONID=" + sessionId + "\r\n" +
            "\r\n";

        assertThat(socket.output()).contains(expected);
    }

    @DisplayName("유효하지 않은 로그인 시 401.html로 리다이렉 한다.")
    @Test
    void postLogin_invalidUser_redirect401() {
        // given
        final String httpRequest = HttpRequestFactory.postLoginWithInvalidUser();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found\r\n" +
            "Location: /401.html\r\n" +
            "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register GET 요청 성공")
    @Test
    void getRegister() throws IOException {
        // given
        final String httpRequest = HttpRequestFactory.getRegister();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String path = "static/register.html";
        final String responseBody = readResource(path);
        final int contentLength = responseBody.getBytes(StandardCharsets.UTF_8).length;

        String expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: " + contentLength + "\r\n" +
            "\r\n" +
            responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register POST 요청 성공")
    @Test
    void postRegister() {
        // given
        final String httpRequest = HttpRequestFactory.postRegisterUser();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found\r\n" +
            "Location: /index.html\r\n" +
            "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
        assertThat(InMemoryUserRepository.findByAccount("newUser")).isNotNull();
    }

    @DisplayName("유효하지 않은 리소스 요청시 404로 리다이렉트한다.")
    @Test
    void invalidUrl_redirect404() {
        // given
        final String httpRequest = HttpRequestFactory.invalidUrl();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found\r\n" +
            "Location: /404.html\r\n" +
            "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String readResource(String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        return new String(
            Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
