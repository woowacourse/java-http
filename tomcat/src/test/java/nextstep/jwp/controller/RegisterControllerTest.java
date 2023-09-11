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

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();
    private final HttpHeaders httpHeadersWithNoSession = HttpHeaders.empty();
    private final HttpHeaders httpHeadersWithSession = HttpHeaders.empty();

    @BeforeEach
    void setUp() {
        String uuid = String.valueOf(UUID.randomUUID());
        httpHeadersWithSession.setCookie(HttpCookie.of("JSESSIONID=" + uuid));
        Session session = new Session(uuid);
        SessionManager.getInstance().add(session);
    }

    @DisplayName("RegisterController는 정해진 request를 핸들링할 수 있다.")
    @ParameterizedTest(name = "method: {0}, target: {1}")
    @CsvSource({
            "POST, /register",
            "GET, /register"
    })
    void canHandle(String method, String target) {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from(method, target, "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        // when
        boolean actual = registerController.canHandle(httpRequest);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("RegisterController는 정해진 request 외의 요청을 핸들링할 수 없다.")
    @ParameterizedTest(name = "method: {0}, target: {1}")
    @CsvSource({
            "POST, /login",
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
        boolean actual = registerController.canHandle(httpRequest);

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("GET 요청 시, register.html 리소스를 반환한다.")
    @Test
    void doGet_hasNoSession() throws IOException {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("GET", "/register", "HTTP/1.1"),
                HttpHeaders.empty(),
                new HashMap<>()
        );

        HttpResponse httpResponse = HttpResponse.init();
        Map<String, String> headers = Map.of(CONTENT_TYPE.getName(), TEXT_HTML.stringifyWithUtf());

        // when
        registerController.doGet(httpRequest, httpResponse);

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

    @DisplayName("유효한 회원가입 정보로 POST 요청 시, 유저를 저장하고 index.html로 redirect 한다.")
    @Test
    void doPost() {
        // given
        HttpRequest httpRequest = HttpRequest.from(
                HttpRequestLine.from("POST", "/login", "HTTP/1.1"),
                httpHeadersWithNoSession,
                Map.of(
                        "account", "new",
                        "password", "password",
                        "email", "new@gmail.com"
                )
        );

        HttpResponse httpResponse = HttpResponse.init();
        Map<String, String> headers = Map.of(LOCATION.getName(), "/index.html");

        // when
        registerController.doPost(httpRequest, httpResponse);

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
}
