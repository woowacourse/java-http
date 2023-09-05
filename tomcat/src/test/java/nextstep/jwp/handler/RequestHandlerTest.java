package nextstep.jwp.handler;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestHandlerTest {

    private final RequestHandler requestHandler = new RequestHandler(new SessionManager());

    @Test
    void html_요청이_오면_content_type은_html() {
        // given
        String request = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        HttpResponse response = HTTP_요청을_보낸다(request);

        // then
        Map<String, String> actual = response.getHeaders();
        assertThat(actual.get("Content-Type")).contains("text/html");
    }

    @Test
    void css_요청이_오면_content_type은_css() throws IOException {
        // given
        String request = String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        HttpResponse response = HTTP_요청을_보낸다(request);

        // then
        Map<String, String> actual = response.getHeaders();
        assertThat(actual.get("Content-Type")).contains("text/css");
    }

    @Test
    void js_요청이_오면_content_type은_js() {
        // given
        String request = String.join("\r\n",
            "GET /js/scripts.js HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        HttpResponse response = HTTP_요청을_보낸다(request);

        // then
        Map<String, String> actual = response.getHeaders();
        assertThat(actual.get("Content-Type")).contains("application/javascript");
    }

    @Test
    void 정상적으로_로그인_성공() {
        // given
        String request = String.join("\r\n",
            "POST /login?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        HttpResponse response = HTTP_요청을_보낸다(request);

        // then
        Map<String, String> headers = response.getHeaders();
        assertAll(
            () -> assertThat(response.getHttpStatus()).isEqualTo(FOUND),
            () -> assertThat(headers.get("Location")).isEqualTo("/index.html")
        );
    }

    @Test
    void 로그인시_쿠키를_반환하고_세션에_담는다() {
        // given
        String request = String.join("\r\n",
            "POST /login?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        HttpResponse response = HTTP_요청을_보낸다(request);

        // then
        Map<String, String> cookie = response.getCookie();
        assertThat(cookie.get("JSESSIONID")).isNotNull();
    }

    @Test
    void 존재하지_않는_아이디로_로그인_요청() {
        // given
        String request = String.join("\r\n",
            "POST /login?account=noUser&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        HttpResponse response = HTTP_요청을_보낸다(request);

        // then
        Map<String, String> headers = response.getHeaders();
        assertAll(
            () -> assertThat(response.getHttpStatus()).isEqualTo(FOUND),
            () -> assertThat(headers.get("Location")).isEqualTo("/401.html")
        );
    }

    @Test
    void 잘못된_비밀번호로_로그인_요청() {
        // given
        String request = String.join("\r\n",
            "POST /login?account=gugu&password=invalidPassword HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        // when
        HttpResponse response = HTTP_요청을_보낸다(request);

        // then
        Map<String, String> headers = response.getHeaders();
        assertAll(
            () -> assertThat(response.getHttpStatus()).isEqualTo(FOUND),
            () -> assertThat(headers.get("Location")).isEqualTo("/401.html")
        );
    }

    @Test
    void 회원가입_성공() {
        // given
        String request = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Content-Length: 46",
            "",
            "account=hs&password=hs123&email=hs%40naver.com");

        // when
        HttpResponse response = HTTP_요청을_보낸다(request);

        // then
        Map<String, String> headers = response.getHeaders();

        assertAll(
            () -> assertThat(headers.get("Location")).isEqualTo("/index.html"),
            () -> assertThat(findByAccount("hs")).isPresent(),
            () -> assertThat(response.getCookie().get("JSESSIONID"))
        );
    }

    private HttpResponse HTTP_요청을_보낸다(String request) {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        try {
            return requestHandler.handle(HttpRequestParser.create(inputStream));
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}
