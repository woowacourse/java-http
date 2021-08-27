package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {
    @Test
    @DisplayName("HTTP Request로부터 URI를 파싱한다.")
    void extractURI() throws IOException {
        // given
        String uri = "/login?account=gugu&password=password";
        String httpRequest = String.join("\r\n",
            "GET " + uri + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest extractor = new HttpRequest(inputStream);

        // when
        String actual = extractor.extractURI();

        // then
        assertThat(extractor.extractURI()).isEqualTo(uri);
    }

    @Test
    @DisplayName("HTTP Request로부터 URI에서 Query String 부분만 파싱한다.")
    void extractURIQueryParams() throws IOException {
        // given
        String uri = "/login?account=gugu&password=password";
        String httpRequest = String.join("\r\n",
            "GET " + uri + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest extractor = new HttpRequest(inputStream);
        Map<String, String> expected = new HashMap<>();
        expected.put("account", "gugu");
        expected.put("password", "password");

        // when
        Map<String, String> queryString = extractor.extractURIQueryParams();

        // then
        assertThat(queryString.size()).isEqualTo(2);
        queryString.forEach((key, value) -> {
            assertThat(queryString.get(key)).isEqualTo(expected.get(key));
        });
    }

    @Test
    @DisplayName("HTTP Request로부터 URI에서 Query String 부분이 아무 것도 안 들어있는 경우 빈 HashMap을 반환한다.")
    void extractURIQueryParams_null() throws IOException {
        // given
        String uri = "/login";
        String httpRequest = String.join("\r\n",
            "GET " + uri + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest extractor = new HttpRequest(inputStream);
        Map<String, String> expected = new HashMap<>();

        // when
        Map<String, String> queryString = extractor.extractURIQueryParams();

        // then
        assertThat(queryString.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("HTTP Request로부터 URI에서 Query String이 있더라도 path 부분만 파싱한다.")
    void extractURIPath() throws IOException {
        // given
        String uri = "/login?account=gugu&password=password";
        String httpRequest = String.join("\r\n",
            "GET " + uri + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest extractor = new HttpRequest(inputStream);

        // when
        String path = extractor.extractURIPath();

        // then
        assertThat(path).isEqualTo("/login");
    }

    @Test
    @DisplayName("HTTP Request로부터 URI에서 Query String이 있더라도, path 부분만 파싱한다.")
    void extractURIPath_notContainsDelimeter() throws IOException {
        // given
        String uri = "/login";
        String httpRequest = String.join("\r\n",
            "GET " + uri + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest extractor = new HttpRequest(inputStream);

        // when
        String path = extractor.extractURIPath();

        // then
        assertThat(path).isEqualTo("/login");
    }

    @Test
    @DisplayName("HTTP Request로부터 URI에서 Query String이 없고 ?만 있더라도, path 부분만 파싱한다.")
    void extractURIPath_containsOnlyDelimiter() throws IOException {
        // given
        String uri = "/login?";
        String httpRequest = String.join("\r\n",
            "GET " + uri + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest extractor = new HttpRequest(inputStream);

        // when
        String path = extractor.extractURIPath();

        // then
        assertThat(path).isEqualTo("/login");
    }

    @Test
    @DisplayName("HTTP Request로부터 HttpMethod를 파싱한다.")
    void extractHttpMethod() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
            "GET /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest extractor = new HttpRequest(inputStream);

        // when
        String method = extractor.extractHttpMethod();

        // then
        assertThat(method).isEqualTo("GET");
    }

    @Test
    @DisplayName("HTTP Request로부터 form-data를 파싱한다.")
    void extractFormData() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest extractor = new HttpRequest(inputStream);
        Map<String, String> expected = new HashMap<>();
        expected.put("account", "gugu");
        expected.put("password", "password");
        expected.put("email", "hkkang%40woowahan.com");

        // when
        Map<String, String> actual = extractor.extractFormData();

        // then
        actual.forEach((key, value) -> {
            assertThat(expected.get(key)).isEqualTo(expected.get(key));
        });
    }
}
