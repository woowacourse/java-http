package nextstep.jwp.network;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HttpRequestTest {

    @DisplayName("HttpRequest 객체를 생성한다 - 성공")
    @Test
    void create() {
        // given
        final String requestAsString = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when // then
        assertThatCode(() -> HttpRequest.of(bufferedReader))
                .doesNotThrowAnyException();
    }

    @DisplayName("HttpRequest에서 method를 추출한다 - 성공")
    @Test
    void getHttpMethod() {
        // given
        final String requestAsString = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        final HttpRequest httpRequest = HttpRequest.of(bufferedReader);
        final HttpMethod actual = httpRequest.getHttpMethod();

        // then
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("HttpRequest에서 URI를 추출한다 - 성공")
    @Test
    void getURI() {
        // given
        final String requestAsString = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */* ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        final HttpRequest httpRequest = HttpRequest.of(bufferedReader);
        final URI actual = httpRequest.getURI();

        // then
        assertThat(actual).isEqualTo(new URI("/register"));
    }

    @DisplayName("HttpRequest에서 body를 추출한다 - 성공")
    @Test
    void getBody() {
        // given
        final String requestAsString = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 58 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");
        final InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        final HttpRequest httpRequest = HttpRequest.of(bufferedReader);
        final Map<String, String> actual = httpRequest.getBody();

        // then
        assertThat(actual)
                .contains(entry("account", "gugu"), entry("password", "password"), entry("email", "hkkang%40woowahan.com"));
    }
}