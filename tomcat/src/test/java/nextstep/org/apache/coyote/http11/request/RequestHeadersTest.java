package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.request.RequestHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("RequestHeaders 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class RequestHeadersTest {

    @Test
    void Header들의_정보를_통해_생성된다() {
        // given
        List<String> headers = List.of(
                "Host: localhost:8080",
                "User-Agent: Insomnia/2023.5.7",
                "Content-Length: 20");

        // when
        RequestHeaders created = RequestHeaders.from(headers);

        // then
        assertThat(created.headers())
                .usingRecursiveComparison()
                .isEqualTo(Map.of(
                        "Host", "localhost:8080",
                        "User-Agent", "Insomnia/2023.5.7",
                        "Content-Length", "20"
                ));
    }

    @Test
    void 비어있을_수_있다() {
        // when
        RequestHeaders requestHeaders = RequestHeaders.from(Collections.emptyList());

        // then
        assertThat(requestHeaders.headers()).isEmpty();
    }

    @Test
    void 특정_헤더가_존재하는지_확인할_수_있다() {
        // given
        List<String> headers = List.of(
                "Host: localhost:8080",
                "User-Agent: Insomnia/2023.5.7",
                "Content-Length: 20");
        RequestHeaders created = RequestHeaders.from(headers);

        // when & then
        assertThat(created.contains("Content-Length")).isTrue();
        assertThat(created.contains("mallang")).isFalse();
    }

    @Test
    void 특정_헤더를_찾을_수_있다() {
        // given
        List<String> headers = List.of(
                "Host: localhost:8080",
                "User-Agent: Insomnia/2023.5.7",
                "Content-Length: 20");
        RequestHeaders created = RequestHeaders.from(headers);

        // when
        String actual = created.get("Content-Length");

        // then
        assertThat(actual).isEqualTo("20");

    }

    @Test
    void 특정_헤더가_없으면_null() {
        // given
        List<String> headers = List.of(
                "Host: localhost:8080",
                "User-Agent: Insomnia/2023.5.7",
                "Content-Length: 20");
        RequestHeaders created = RequestHeaders.from(headers);

        // when
        String actual = created.get("mallang");

        // then
        assertThat(actual).isNull();
    }
}
