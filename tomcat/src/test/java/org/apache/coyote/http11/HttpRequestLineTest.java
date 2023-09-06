package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpRequestLineTest {

    @Test
    void 유효하지_않은_requestLine_형식이면_에외() {
        // given
        String requestLine = "GET /search?key1=value1&key2=value2 HTTP/1.1 helloDummyData";

        // when & then
        assertThatThrownBy(() -> new RequestLine(requestLine))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("http 요청 라인은 3가지 요소로 구성되어야 합니다.");
    }

    @Test
    void METHOD_의_타입을_알_수_있다() {
        // given
        String requestLine = "GET /search?key1=value1&key2=value2 HTTP/1.1";

        // when
        RequestLine actual = new RequestLine(requestLine);

        // then
        assertThat(actual.getMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    void METHOD_이_유효하지_않으면_예외() {
        // given
        String requestLine = "HELLO /search?key1=value1&key2=value2 HTTP/1.1";

        // when & then
        assertThatThrownBy(() -> new RequestLine(requestLine))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("지원하지 않는 메서드 입니다.");
    }

    @Test
    void requestPath를_알_수_있다() {
        // given
        String requestLine = "GET /search?key1=value1&key2=value2 HTTP/1.1";

        // when
        RequestLine actual = new RequestLine(requestLine);

        // then
        assertThat(actual.getPath()).isEqualTo("/search");
    }

    @Test
    void queryParam을_알_수_있다() {
        // given
        String requestLine = "GET /search?key1=value1&key2=value2 HTTP/1.1";

        // when
        RequestLine actual = new RequestLine(requestLine);

        HashMap<String, String> expected = new HashMap<>();
        expected.put("key1", "value1");
        expected.put("key2", "value2");

        // then
        assertThat(actual.getQueryParam()).usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    void queryParamd의_값이_등호로_나누어지지_않으면_예외() {
        // given
        String requestLine = "GET /search?key1" + "?" + "value1&key2=value2 HTTP/1.1";

        // when & then
        assertThatThrownBy(() -> new RequestLine(requestLine))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유효하지 않은 key value 형식 입니다.");
    }
}
