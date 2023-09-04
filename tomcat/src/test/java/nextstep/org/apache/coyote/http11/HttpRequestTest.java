package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpRequestTest {

    @Test
    void HTTP_요청의_uri를_반환한다() throws IOException {
        // given
        String httpRequest= String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        String actual = request.getUri();

        // then
        assertThat(actual).isEqualTo("/index.html");
    }

    @Test
    void uri는_query_string_없이_반환한다() throws IOException {
        // given
        String httpRequest= String.join("\r\n",
            "GET /blackCat?age=30&name=wooseok HTTP/1.1 ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        String actual = request.getUri();

        // then
        assertThat(actual).isEqualTo("/blackCat");
    }

    @Test
    void query_string을_반환한다() throws IOException {
        // given
        String httpRequest= String.join("\r\n",
            "GET /blackCat?age=30&name=wooseok HTTP/1.1 ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        Map<String, String> queryString = request.getQueryString();

        // then
        assertAll(
            () -> assertThat(queryString.get("age")).isEqualTo("30"),
            () -> assertThat(queryString.get("name")).isEqualTo("wooseok"),
            () -> assertThat(queryString).hasSize(2)
        );
    }

    @Test
    void query_string이_없는데_요청시_예외() throws IOException {
        // given
        String httpRequest= String.join("\r\n",
            "GET /blackCat HTTP/1.1 ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when & then
        assertThatThrownBy(() -> request.getQueryString())
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void body를_반환한다() throws IOException {
        // given
        String httpRequest= String.join("\r\n",
            "GET /blackCat HTTP/1.1 ",
            "Content-Length: 33",
            "",
            "name=hyunseo&password=hyunseo1234");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        Map<String, String> body = request.getBody();

        // then
        assertAll(
            () -> assertThat(body.get("name")).isEqualTo("hyunseo"),
            () -> assertThat(body.get("password")).isEqualTo("hyunseo1234"),
            () -> assertThat(body).hasSize(2)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "GET"})
    void HTTP_method를_반환한다(String method) throws IOException {
        // given
        String httpRequest = String.join("\r\n",
            method + " /blackCat HTTP/1.1 ",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        HttpMethod actual = request.getMethod();

        // then
        assertThat(actual.name()).isEqualTo(method);
    }
}
