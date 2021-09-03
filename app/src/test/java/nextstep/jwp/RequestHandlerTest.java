package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestHandlerTest {

    @BeforeEach
    void setUp() {
        HttpSession httpSession = HttpSessions.getSession("1234");
        httpSession.removeAttribute("user");
    }

    @DisplayName("GET /index.html 요청시 파일 불러오기")
    @Test
    void index() throws IOException {
        // given
        final MockSocket socket = getMockSocket("/index.html");
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        assertThat(socket.output()).isEqualTo(getExpected(5564, resource));
    }

    @DisplayName("GET /login 요청시 login.html 불러오기")
    @Test
    void getLogin() throws IOException {
        // given
        final MockSocket socket = getMockSocket("/login");
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");

        assertThat(socket.output()).isEqualTo(getExpected(3797, resource));
    }

    @DisplayName("GET /register 요청시 register.html 불러오기")
    @Test
    void getRegister() throws IOException {
        // given
        final MockSocket socket = getMockSocket("/register.html");
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");

        assertThat(socket.output()).isEqualTo(getExpected(4319, resource));
    }

    @DisplayName("POST /login 요청시 로그인")
    @Test
    void postLogin() {
        //given
        String body = "account=asdf&password=password ";
        final MockSocket socket = postMockSocket("/login", body);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // then
        assertThatThrownBy(requestHandler::run).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 유저입니다. 회원가입을 해주세요");

    }

    @DisplayName("POST /register 요청시 회원가입")
    @Test
    void postRegister() {
        //given
        String body = "account=younge&password=password&email=abc%40abc.com ";
        final MockSocket socket = postMockSocket("/register", body);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        User newUser = InMemoryUserRepository.findByAccount("younge").orElseThrow();

        assertThat("younge").isEqualTo(newUser.getAccount());
    }

    @DisplayName("GET /login 요청시 이미 로그인 된 경우 /index.html로 리다이렉트")
    @Test
    void loginCheck() throws IOException {
        // given
        HttpSession httpSession = HttpSessions.getSession("1234");
        User user = InMemoryUserRepository.findByAccount("gugu").orElseThrow();
        httpSession.setAttribute("user", user);

        final MockSocket socket = getMockSocket("/login");
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).isEqualTo(redirectExpected());
    }

    private MockSocket postMockSocket(String requestUri, String body) {
        final String httpRequest = String.join("\r\n",
            "POST "+ requestUri +" HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + body.length() +" ",
            "Content-Type: application/x-www-form-urlencoded ",
            "Accept: */* ",
            "Cookie: JSESSIONID=1234",
            "",
            body
        );

        return new MockSocket(httpRequest);
    }

    private MockSocket getMockSocket(String url) {
        final String httpRequest = String.join("\r\n",
            "GET " + url + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Cookie: JSESSIONID=1234",
            "",
            "");

        return new MockSocket(httpRequest);
    }

    private String getExpected(int length, URL resource) throws IOException {
        return "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: " + length + " \r\n" +
            "\r\n"+
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String redirectExpected() {
        return "HTTP/1.1 302 Found \r\n" +
        "Location: /index.html \r\n" +
            "";
    }
}
