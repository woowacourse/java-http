package nextstep.jwp.controller;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.coyote.http11.common.HttpHeaderType.*;
import static org.apache.coyote.http11.common.MediaType.TEXT_HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();
    private final HttpHeaders httpHeadersWithNoSession = HttpHeaders.empty();
    private final HttpHeaders httpHeadersWithSession = HttpHeaders.empty();

    @BeforeEach
    void setUp() {
        String uuid = String.valueOf(UUID.randomUUID());
        httpHeadersWithSession.setCookie(HttpCookie.of("JSESSIONID=" + uuid));
        Session session = new Session(uuid);
        SessionManager.getInstance().add(session);
    }

    @DisplayName("LoginController는 정해진 request를 핸들링할 수 있다.")
    @ParameterizedTest(name = "method: {0}, target: {1}")
    @CsvSource({
            "POST, /login",
            "GET, /login"
    })
    void canHandle(String method, String target) {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from(method, target, "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        // when
        boolean actual = loginController.canHandle(httpRequest);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("LoginController는 정해진 request 외의 요청을 핸들링할 수 없다.")
    @ParameterizedTest(name = "method: {0}, target: {1}")
    @CsvSource({
            "POST, /register",
            "GET, /"
    })
    void canHandle_fail(String method, String target) {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from(method, target, "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        // when
        boolean actual = loginController.canHandle(httpRequest);

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("세션이 없는 경우 GET 요청 시, login.html 리소스를 반환한다.")
    @Test
    void doGet_hasNoSession() throws IOException {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("GET", "/login", "HTTP/1.1"),
                httpHeadersWithNoSession,
                new HashMap<>()
        );

        HttpResponse httpResponse = HttpResponse.init();
        Map<String, String> headers = Map.of(CONTENT_TYPE.getName(), TEXT_HTML.stringifyWithUtf());

        // when
        loginController.doGet(httpRequest, httpResponse);

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpResponse).extracting("statusCode")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpStatusCode.OK);
                    softly.assertThat(httpResponse).extracting("headers")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpHeaders.of(headers));
                }
        );
    }

    @DisplayName("세션이 이미 있는 경우 GET 요청 시, index.html로 redirect 한다.")
    @Test
    void doGet_alreadyHasSession() throws IOException {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("GET", "/login", "HTTP/1.1"),
                httpHeadersWithSession,
                new HashMap<>()
        );

        HttpResponse httpResponse = HttpResponse.init();
        Map<String, String> headers = Map.of(LOCATION.getName(), "/index.html");

        // when
        loginController.doGet(httpRequest, httpResponse);

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpResponse).extracting("statusCode")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpStatusCode.FOUND);
                    softly.assertThat(httpResponse).extracting("headers")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpHeaders.of(headers));
                }
        );
    }

    @DisplayName("유효하지 않은 계정으로 POST 요청 시, 401.html로 redirect 한다.")
    @Test
    void doPost_invalidAccount() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("POST", "/login", "HTTP/1.1"),
                httpHeadersWithNoSession,
                Map.of(
                        "account", "invalid_user",
                        "password", "invalid_password"
                )
        );

        HttpResponse httpResponse = HttpResponse.init();
        Map<String, String> headers = Map.of(LOCATION.getName(), "/401.html");

        // when
        loginController.doPost(httpRequest, httpResponse);

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpResponse).extracting("statusCode")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpStatusCode.FOUND);
                    softly.assertThat(httpResponse).extracting("headers")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpHeaders.of(headers));
                }
        );
    }

    @DisplayName("세션이 이미 있는 경우 POST 요청 시, index.html로 redirect 한다.")
    @Test
    void doPost_alreadyHasSession() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("POST", "/login", "HTTP/1.1"),
                httpHeadersWithSession,
                Map.of(
                        "account", "gugu",
                        "password", "password"
                )
        );

        HttpResponse httpResponse = HttpResponse.init();
        Map<String, String> headers = Map.of(LOCATION.getName(), "/index.html");

        // when
        loginController.doPost(httpRequest, httpResponse);

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpResponse).extracting("statusCode")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpStatusCode.FOUND);
                    softly.assertThat(httpResponse).extracting("headers")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpHeaders.of(headers));
                }
        );
    }

    @DisplayName("세션이 없고, 유효한 유저의 POST 요청 시, set-cookie 설정 후 index.html로 redirect 한다.")
    @Test
    void doPost_hasNoSessionAndValidUser() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("POST", "/login", "HTTP/1.1"),
                httpHeadersWithNoSession,
                Map.of(
                        "account", "gugu",
                        "password", "password"
                )
        );

        HttpResponse httpResponse = HttpResponse.init();
        Map<String, String> headers = Map.of(
                SET_COOKIE.getName(), "JSESSIONID=",
                LOCATION.getName(), "/index.html"
        );

        // when
        loginController.doPost(httpRequest, httpResponse);

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpResponse).extracting("statusCode")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpStatusCode.FOUND);
                    softly.assertThat(httpResponse).extracting("headers")
                            .usingRecursiveComparison()
                            .ignoringFields( "headers.Set-Cookie")
                            .isEqualTo(HttpHeaders.of(headers));
                }
        );
    }
}
