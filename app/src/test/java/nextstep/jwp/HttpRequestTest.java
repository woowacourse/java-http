package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
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

        Map<String, String> expected = new HashMap<>();

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
}
