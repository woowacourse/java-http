package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("isEmptyLine()을 통해 해당 HttpRequest가 빈 값인지 확인한다.")
    void isEmptyLine() {
        HttpRequest httpRequest = new HttpRequest(null, new ArrayList<>(), null);
        assertThat(httpRequest.isEmptyLine()).isTrue();
    }

    @Test
    @DisplayName("extractURI()를 통해 HTTP Request로부터 URI를 파싱한다.")
    void extractURI() {
        // given
        String expected = "/login?account=gugu&password=password";
        String statusLine = "GET " + expected + " HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);

        // when
        String actual = httpRequest.extractURI();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("HTTP Request로부터 URI에서 Query String 부분만 파싱한다.")
    void extractURIQueryParams() {
        // given
        String statusLine = "GET /login?account=gugu&password=password HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);
        Map<String, String> expected = new HashMap<>();
        expected.put("account", "gugu");
        expected.put("password", "password");

        // when
        Map<String, String> actual = httpRequest.extractURIQueryParams();

        // then
        assertThat(actual).hasSize(2);
        expected.forEach((key, value) -> {
            assertThat(actual).containsEntry(key, value);
        });
    }

    @Test
    @DisplayName("HTTP Request로부터 URI에서 Query String 부분이 아무 것도 안 들어있는 경우 빈 HashMap을 반환한다.")
    void extractURIQueryParams_null() {
        // given
        String statusLine = "GET /login HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);

        // when
        Map<String, String> queryString = httpRequest.extractURIQueryParams();

        // then
        assertThat(queryString).isEmpty();
    }

    @Test
    @DisplayName("HTTP Request로부터 URI에서 Query String이 있더라도 path 부분만 파싱한다.")
    void extractURIPath() {
        // given
        String statusLine = "GET /login HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);

        // when
        String path = httpRequest.extractURIPath();

        // then
        assertThat(path).isEqualTo("/login");
    }


    @Test
    @DisplayName("HTTP Request로부터 URI에서 Query String이 없고 ?만 있더라도, path 부분만 파싱한다.")
    void extractURIPath_containsOnlyDelimiter() {
        // given
        String statusLine = "GET /login? HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);

        // when
        String path = httpRequest.extractURIPath();

        // then
        assertThat(path).isEqualTo("/login");
    }

    @Test
    @DisplayName("HTTP Request로부터 HttpMethod를 파싱한다.")
    void extractHttpMethod() {
        // given
        String statusLine = "GET /login HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);

        // when
        HttpMethod method = httpRequest.extractHttpMethod();

        // then
        assertThat(method).isEqualTo(HttpMethod.GET);
    }

    @Test
    @DisplayName("HTTP Request로부터 form-data를 파싱한다.")
    void extractFormData() {
        // given
        String bodyLine = "account=gugu&password=password&email=hkkang%40woowahan.com";
        HttpRequest httpRequest = new HttpRequest(null, new ArrayList<>(), bodyLine);
        Map<String, String> expected = new HashMap<>();
        expected.put("account", "gugu");
        expected.put("password", "password");
        expected.put("email", "hkkang%40woowahan.com");

        // when
        Map<String, String> actual = httpRequest.extractFormData();

        // then
        expected.forEach((key, value) -> {
            assertThat(actual).containsEntry(key, value);
        });
    }

    @Test
    @DisplayName("HttpMethod가 GET일 경우 true를 반환한다")
    void isGet() {
        String statusLine = "GET /login HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);
        assertThat(httpRequest.isGet()).isTrue();
    }

    @Test
    @DisplayName("HttpMethod가 POST일 경우 true를 반환한다")
    void isPost() {
        String statusLine = "POST /login HTTP/1.1 ";
        HttpRequest httpRequest = new HttpRequest(statusLine, new ArrayList<>(), null);
        assertThat(httpRequest.isPost()).isTrue();
    }

    @Test
    @DisplayName("Header의 정보를 Map으로 추출한다")
    void extractHeaders() {
        // given
        List<String> headerLines = Arrays.asList(
            "Host: localhost:8080",
            "Connection: keep-alive"
        );
        HttpRequest httpRequest = new HttpRequest(null, headerLines, null);
        Map<String, String> expected = new HashMap<>();
        expected.put("Host", "localhost:8080");
        expected.put("Connection", "keep-alive");

        // when
        Map<String, String> actual = httpRequest.getHeaders();

        // then
        expected.forEach((key, value) -> {
            assertThat(actual).containsEntry(key, value);
        });
    }

    @Test
    @DisplayName("Cookie의 정보를 Map으로 추출한다")
    void extractCookies() {
        // given
        List<String> headerLines = Arrays.asList(
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );
        HttpRequest httpRequest = new HttpRequest(null, headerLines, null);
        Map<String, String> expected = new HashMap<>();
        expected.put("yummy_cookie", "choco");
        expected.put("tasty_cookie", "strawberry");
        expected.put("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");

        // when
        Map<String, String> actual = httpRequest.extractCookies();

        // then
        expected.forEach((key, value) -> {
            assertThat(actual).containsEntry(key, value);
        });
    }

    @Test
    @DisplayName("JSESSIONID가 있으면 true를 반환한다.")
    void containsJSessionId_true() {
        // given
        List<String> headerLines = Arrays.asList(
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );
        HttpRequest httpRequest = new HttpRequest(null, headerLines, null);

        // when
        boolean actual = httpRequest.containsJSessionId();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("JSESSIONID가 있으면 false를 반환한다.")
    void containsJSessionId_false() {
        // given
        List<String> headerLines = Arrays.asList(
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry"
        );
        HttpRequest httpRequest = new HttpRequest(null, headerLines, null);

        // when
        boolean actual = httpRequest.containsJSessionId();

        // then
        assertThat(actual).isFalse();
    }
}
