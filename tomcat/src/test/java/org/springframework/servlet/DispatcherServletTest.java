package org.springframework.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.http.BasicHttpRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.info.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.richard.utils.ResourceUtils;
import org.springframework.http.Cookie;

class DispatcherServletTest {

    private final DispatcherServlet dispatcherServlet = new DispatcherServlet();

    @DisplayName("DispatcherServlet은 모든 요청을 처리할 단 하나의 서블릿이므로 support 메서드가 항상 true를 반환한다")
    @Test
    void dispatcherServlet_supports_every_request() {
        // given
        final HttpRequest welcomePage = new BasicHttpRequest(HttpMethod.GET, "/", null, null, null);
        final HttpRequest indexPage = new BasicHttpRequest(HttpMethod.GET, "/index.html", null, null, null);
        final HttpRequest loginPage = new BasicHttpRequest(HttpMethod.GET, "/login", null, null, null);
        final HttpRequest loginRequest = new BasicHttpRequest(HttpMethod.POST, "/login", null, null, null);
        final HttpRequest signUpPage = new BasicHttpRequest(HttpMethod.GET, "/register", null, null, null);
        final HttpRequest signUpRequest = new BasicHttpRequest(HttpMethod.POST, "/register", null, null, null);

        // when
        final boolean supportAll = Stream.of(welcomePage, indexPage, loginPage, loginRequest, signUpPage, signUpRequest)
                .allMatch(dispatcherServlet::support);

        // then
        assertThat(supportAll).isTrue();
    }

    @DisplayName("doService 메서드는")
    @Nested
    class DoService {

        @DisplayName("/ 경로로 GET 요청을 받으면 Hello world!를 응답한다")
        @Test
        void welcomePage() {
            // given
            final HttpRequest welcomePage =
                    new BasicHttpRequest(HttpMethod.GET, "/", null, null, null);
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 12 ",
                    "",
                    "Hello world!"
            );

            // when
            final HttpResponse httpResponse = dispatcherServlet.doService(welcomePage);
            final String actual = httpResponse.getResponseHttpMessage();

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("/register 경로로 GET 요청을 받으면 register.html을 응답한다")
        @Test
        void registerPage() {
            // given
            final HttpRequest registerPageRequest =
                    new BasicHttpRequest(HttpMethod.GET, "/register", null, null, null);
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 4319 ",
                    "",
                    ResourceUtils.createResourceAsString("static/register.html")
            );

            // when
            final HttpResponse httpResponse = dispatcherServlet.doService(registerPageRequest);
            final String actual = httpResponse.getResponseHttpMessage();

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("/register 경로로 POST 요청을 받으면 회원가입 처리 후 index.html 로 리디렉션을 응답한다")
        @Test
        void registerRequest() {
            // given
            final HttpRequest registerPageRequest = new BasicHttpRequest(
                    HttpMethod.POST,
                    "/register",
                    null,
                    null,
                    Map.of("email", "email@email.com", "account", "account", "password", "password")
            );

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: index.html ",
                    ""
            );

            // when
            final HttpResponse httpResponse = dispatcherServlet.doService(registerPageRequest);
            final String actual = httpResponse.getResponseHttpMessage();
            final Optional<User> registeredAccount =
                    InMemoryUserRepository.findByAccountAndPassword("account", "password");

            // then
            assertAll(
                    () -> assertThat(registeredAccount).isPresent(),
                    () -> assertThat(actual).isEqualTo(expected)
            );
        }

        @DisplayName("/login 경로로 GET 요청을 받으면, 쿠키 존재 확인 후 없을 시 login.html을 응답한다")
        @Test
        void loginPageWithoutCookie() {
            // given
            final HttpRequest registerPageRequest = new BasicHttpRequest(
                    HttpMethod.GET,
                    "/login",
                    new HashMap<>(),
                    new HashMap<>(),
                    null
            );

            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 3448 ",
                    "",
                    ResourceUtils.createResourceAsString("static/login.html")
            );

            // when
            final HttpResponse httpResponse = dispatcherServlet.doService(registerPageRequest);
            final String actual = httpResponse.getResponseHttpMessage();

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("/login 경로로 GET 요청을 받으면, 쿠키 존재 확인 후 유효한 쿠키가 있으면 index.html 리디렉팅을 응답한다")
        @Test
        void loginPageWithValidCookie() {
            // given
            final Session session = Session.newInstance();

            final String cookieString = Cookie
                    .builder()
                    .name("JSESSIONID")
                    .value(session.getId())
                    .build()
                    .getCookieString();

            final HttpRequest registerPageRequest = new BasicHttpRequest(
                    HttpMethod.GET,
                    "/login",
                    Map.of("Cookie", cookieString),
                    new HashMap<>(),
                    null
            );

            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: index.html ",
                    ""
            );

            // when
            final HttpResponse httpResponse = dispatcherServlet.doService(registerPageRequest);
            final String actual = httpResponse.getResponseHttpMessage();

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("/login 경로로 GET 요청을 받으면, 쿠키 존재 확인 후 쿠키가 유효하지 않을 경우 login.html을 응답한다")
        @Test
        void loginPageWithInvalidCookie() {
            // given
            final Session session = Session.newInstance();
            SessionManager.remove(session.getId());

            final String cookieString = Cookie
                    .builder()
                    .name("JSESSIONID")
                    .value(session.getId())
                    .build()
                    .getCookieString();

            final HttpRequest registerPageRequest = new BasicHttpRequest(
                    HttpMethod.GET,
                    "/login",
                    Map.of("Cookie", cookieString),
                    new HashMap<>(),
                    null
            );

            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 3448 ",
                    "",
                    ResourceUtils.createResourceAsString("static/login.html")
            );

            // when
            final HttpResponse httpResponse = dispatcherServlet.doService(registerPageRequest);
            final String actual = httpResponse.getResponseHttpMessage();

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("/login 경로로 POST 요청을 받으면 정상 로그인 성공시 Set-Cookie 헤더와 함께 index.html을 리디렉팅한다")
        @Test
        void loginRequestSuccess() {
            // given
            final String account = "gugugu";
            final String password = "passpassword";
            final User user = new User(2L, account, password, "gugugu@gugu.gu");
            InMemoryUserRepository.save(user);

            final HttpRequest registerPageRequest = new BasicHttpRequest(
                    HttpMethod.POST,
                    "/login",
                    null,
                    null,
                    Map.of("account", account, "password", password)
            );

            final List<String> expected = List.of(
                    "HTTP/1.1 302 Found",
                    "Set-Cookie: JSESSIONID=",
                    "Location: index.html"
            );

            // when
            final HttpResponse httpResponse = dispatcherServlet.doService(registerPageRequest);
            final String actual = httpResponse.getResponseHttpMessage();

            // then
            assertThat(actual).contains(expected);
        }

        @DisplayName("/login 경로로 POST 요청을 받으면 정상 로그인 실패시 index.html을 리디렉팅한다")
        @Test
        void loginRequestFail() {
            // given
            final HttpRequest registerPageRequest = new BasicHttpRequest(
                    HttpMethod.POST,
                    "/login",
                    null,
                    null,
                    Map.of("account", "no account", "password", "no password")
            );

            final String expected = String.join("\r\n",
                    "HTTP/1.1 401 Unauthorized ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 2426 ",
                    "",
                    ResourceUtils.createResourceAsString("static/401.html")
            );

            // when
            final HttpResponse httpResponse = dispatcherServlet.doService(registerPageRequest);
            final String actual = httpResponse.getResponseHttpMessage();

            // then
            assertThat(actual).isEqualTo(expected);
        }
    }
}
