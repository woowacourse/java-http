package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("RequestLine을 파싱한다.")
    void from() {
        // given
        final String line = "GET /index.html?name=gugu&age=10 HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(line);

        // then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getPath()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.getQueryParams()).hasSize(2),
                () -> assertThat(requestLine.getVersion()).isEqualTo(HttpVersion.HTTP_1_1)
        );
    }

    @Test
    @DisplayName("쿼리 파라미터가 없는 RequestLine을 파싱한다.")
    void from_no_query_params() {
        // given
        final String line = "GET /index.html HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(line);

        // then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getPath()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.getQueryParams()).isEmpty(),
                () -> assertThat(requestLine.getVersion()).isEqualTo(HttpVersion.HTTP_1_1)
        );
    }

    @Test
    @DisplayName("URL 인코딩된 쿼리 파라미터를 파싱한다.")
    void parseUrlEncodedParams() {
        // given
        final String data = "name=gugu&age=10&country=korea&hobby=programming&hobby=reading";

        // when
        final Map<String, List<String>> params = RequestLine.parseUrlEncodedParams(data);

        // then
        assertAll(
                () -> assertThat(params).hasSize(4),
                () -> assertThat(params.get("name")).containsOnly("gugu"),
                () -> assertThat(params.get("age")).containsOnly("10"),
                () -> assertThat(params.get("country")).containsOnly("korea"),
                () -> assertThat(params.get("hobby")).containsExactly("programming", "reading")
        );
    }

    @Test
    @DisplayName("값이 없는 쿼리 파라미터를 파싱한다.")
    void parseUrlEncodedParams_no_value() {
        // given
        final String data = "name=&age=10";

        // when
        final Map<String, List<String>> params = RequestLine.parseUrlEncodedParams(data);

        // then
        assertAll(
                () -> assertThat(params).hasSize(2),
                () -> assertThat(params.get("name")).containsOnly(""),
                () -> assertThat(params.get("age")).containsOnly("10")
        );
    }

    @Test
    @DisplayName("값이 =를 포함하는 쿼리 파라미터를 파싱한다.")
    void parseUrlEncodedParams_contains_equals() {
        // given
        final String data = "a=b=c&d=e";

        // when
        final Map<String, List<String>> params = RequestLine.parseUrlEncodedParams(data);

        // then
        assertAll(
                () -> assertThat(params).hasSize(2),
                () -> assertThat(params.get("a")).containsOnly("b=c"),
                () -> assertThat(params.get("d")).containsOnly("e")
        );
    }

    @Test
    @DisplayName("잘못된 인코딩을 포함하는 쿼리 파라미터를 파싱한다.")
    void parseUrlEncodedParams_invalid_encoding() {
        // given
        final String data = "a=1&b=%invalid&c=3";

        // when
        final Map<String, List<String>> params = RequestLine.parseUrlEncodedParams(data);

        // then
        assertAll(
                () -> assertThat(params).hasSize(2),
                () -> assertThat(params.get("a")).containsOnly("1"),
                () -> assertThat(params.get("c")).containsOnly("3")
        );
    }
}
