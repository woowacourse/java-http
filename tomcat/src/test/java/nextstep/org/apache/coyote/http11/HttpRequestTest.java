package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import nextstep.jwp.handler.HttpRequestLine;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
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
            null,
            null,
            null
        );

        // when
        String actual = request.getUri();

        // then
        assertThat(actual).isEqualTo("/index.html");
    }

    @Test
    void uri는_query_string_없이_반환한다() {
        // given
        HttpRequest request = new HttpRequest(
            new HttpRequestLine("get", "/blackCat?age=30&name=wooseok", null),
            null,
            null,
            null
        );

        // when
        String actual = request.getUri();

        // then
        assertThat(actual).isEqualTo("/blackCat");
    }

    @Test
    void query_string을_반환한다() {
        // given
        HttpRequest request = new HttpRequest(
            new HttpRequestLine("get", "/blackCat?age=30&name=wooseok", null),
            null,
            null,
            null
        );

        // when
        Map<String, String> actual = request.getQueryString();

        // then
        assertAll(
            () -> assertThat(actual.get("age")).isEqualTo("30"),
            () -> assertThat(actual.get("name")).isEqualTo("wooseok"),
            () -> assertThat(actual).hasSize(2)
        );
    }

    @Test
    void query_string이_없는데_요청시_예외() {
        // given
        HttpRequest request = new HttpRequest(
            new HttpRequestLine("get", "/index.html", null),
            null,
            null,
            null
        );

        // when & then
        assertThatThrownBy(() -> request.getQueryString())
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void body를_반환한다() {
        // given
        HttpRequest request = new HttpRequest(
            new HttpRequestLine("get", null, null), null, null, "name=hyunseo&password=hyunseo1234");

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
            new HttpRequestLine(method, null, null),
            null,
            null,
            null
        );

        // when
        HttpMethod actual = request.getMethod();

        // then
        assertThat(actual.name()).isEqualTo(method);
    }
}
