package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestLine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpRequestTest {

    @Test
    void HTTP_요청의_uri를_반환한다() {
        // given
        HttpRequest request = new HttpRequest(
            new HttpRequestLine("get", "/index.html", null),
            Map.of(),
            Map.of(),
            Map.of(),
            ""
        );

        // when
        String actual = request.getPath();

        // then
        assertThat(actual).isEqualTo("/index.html");
    }

    @Test
    void uri는_query_string_없이_반환한다() {
        // given
        HttpRequest request = new HttpRequest(
            new HttpRequestLine("get", "/blackCat?age=30&name=wooseok", null),
            Map.of(),
            Map.of(),
            Map.of(),
            ""
        );

        // when
        String actual = request.getPath();

        // then
        assertThat(actual).isEqualTo("/blackCat");
    }

    @Test
    void body를_반환한다() {
        // given
        HttpRequest request = new HttpRequest(
            new HttpRequestLine("get", "path", null),
            Map.of(),
            Map.of(),
            Map.of(),
            "name=hyunseo&password=hyunseo1234");

        // when
        String actual = request.getBody();

        // then
        assertThat(actual).isEqualTo("name=hyunseo&password=hyunseo1234");
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "GET"})
    void HTTP_method를_반환한다(String method) {
        // given
        HttpRequest request = new HttpRequest(
            new HttpRequestLine(method, "path", null),
            Map.of(),
            Map.of(),
            Map.of(),
            ""
        );

        // when
        HttpMethod actual = request.getMethod();

        // then
        assertThat(actual.name()).isEqualTo(method);
    }
}
