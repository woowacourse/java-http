package nextstep.org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpRequestTest {

    @Test
    @DisplayName("정적 팩토리 메서드 of는 http 메시지 시작줄을 파싱해서 객체를 생성한다.")
    void of() throws IOException {
        // given
        final String startLine = "GET / HTTP/1.1 ";
        final String httpHeader = String.join("\r\n",
                "Content-Length: 999 ",
                "Cookie: JSESSIONID=1q2w3e4r ",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(httpHeader.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final HttpHeader header = HttpHeader.from(bufferedReader);

        // when & then
        assertThatCode(() -> HttpRequest.of(startLine, header))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "getPath 메서드는 http 요청 경로가 {0}이면 {1}을 반환한다.")
    @CsvSource(value = {"/,/", "/login.html,/login.html", "/login?user=rick&password=123,/login"})
    void getPath(final String path, final String expected) throws IOException {
        // given
        final HttpRequest httpRequest = getHttpRequest("GET", path, "sessionId");

        // when
        final String actual = httpRequest.getPath();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest(name = "isRegister 메서드는 http 요청이 '회원가입 post' 요청인지 확인한다.")
    @CsvSource(value = {"POST,/register,true", "GET,/register,false", "POST,/login,false"})
    void isRegister(final String httpMethod, final String path, final boolean expected) throws IOException {
        // given
        final HttpRequest httpRequest = getHttpRequest(httpMethod, path, "sessionId");

        // when
        final boolean actual = httpRequest.isRegister();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest(name = "isLogin 메서드는 http 요청이 '로그인 post' 요청인지 확인한다.")
    @CsvSource(value = {"POST,/login,true", "GET,/login,false", "POST,/register,false"})
    void isLogin(final String httpMethod, final String path, final boolean expected) throws IOException {
        // given
        final HttpRequest httpRequest = getHttpRequest(httpMethod, path, "sessionId");

        // when
        final boolean actual = httpRequest.isLogin();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest(name = "isLoginPage 메서드는 http '요청이 로그인 get' 요청인지 확인한다.")
    @CsvSource(value = {"GET,/login,true", "POST,/login,false", "POST,/register,false"})
    void isLoginPage(final String httpMethod, final String path, final boolean expected) throws IOException {
        // given
        final HttpRequest httpRequest = getHttpRequest(httpMethod, path, "sessionId");

        // when
        final boolean actual = httpRequest.isLoginPage();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    @DisplayName("alreadyLogin 메서드는")
    class AlreadyLogin {

        @Test
        @DisplayName("이미 로그인했으면 true를 반환한다.")
        void success() throws IOException {
            // given
            final User user = new User("rick", "1q2w3e4r", "rick@levellog.app");
            InMemoryUserRepository.save(user);

            final Session session = Session.generate();
            session.setAttribute("user", user);
            SessionManager.add(session);

            final HttpRequest httpRequest = getHttpRequest("GET", "/", session.getId());

            // when
            final boolean actual = httpRequest.alreadyLogin();

            // then
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("Cookie에 JSESSIONID가 없으면 false를 반환한다.")
        void alreadyLogin_false_noSessionId() throws IOException {
            // given
            final String startLine = "GET / HTTP/1.1 ";
            final String httpHeader = String.join("\r\n",
                    "Content-Length: 999 ",
                    "",
                    "");

            final InputStream inputStream = new ByteArrayInputStream(httpHeader.getBytes());
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final HttpHeader header = HttpHeader.from(bufferedReader);

            final HttpRequest httpRequest = HttpRequest.of(startLine, header);

            // when
            final boolean actual = httpRequest.alreadyLogin();

            // then
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("session에 로그인한 유저의 정보가 저장되어있지 않으면 false를 반환한다.")
        void alreadyLogin_false_userNotInSession() throws IOException {
            // given
            final Session session = Session.generate();
            SessionManager.add(session);

            final HttpRequest httpRequest = getHttpRequest("GET", "/", session.getId());

            // when
            final boolean actual = httpRequest.alreadyLogin();

            // then
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("session에 저장된 유저의 정보가 db에 존재하지 않으면 false를 반환한다.")
        void alreadyLogin_false_userNotInRepository() throws IOException {
            // given
            final User user = new User("아스피", "1q2w3e4r", "아스피@levellog.app");

            final Session session = Session.generate();
            session.setAttribute("user", user);
            SessionManager.add(session);

            final HttpRequest httpRequest = getHttpRequest("GET", "/", session.getId());

            // when
            final boolean actual = httpRequest.alreadyLogin();

            // then
            assertThat(actual).isFalse();
        }
    }

    private HttpRequest getHttpRequest(final String httpMethod, final String path, final String sessionId)
            throws IOException {
        final String startLine = httpMethod + " " + path + " HTTP/1.1 ";
        final String httpHeader = String.join("\r\n",
                "Content-Length: 999 ",
                "Cookie: JSESSIONID=" + sessionId + " ",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(httpHeader.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final HttpHeader header = HttpHeader.from(bufferedReader);

        return HttpRequest.of(startLine, header);
    }
}
