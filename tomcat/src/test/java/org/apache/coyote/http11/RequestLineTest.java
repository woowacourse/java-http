package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class RequestLineTest {

    @DisplayName("쿼리 파라미터가 없는 요청 라인을 파싱할 수 있다.")
    @Test
    void from_noQueryParams() {
        // given
        final String requestLineString = "GET /index.html HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(requestLineString);

        // then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getPath()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.getQueryParams()).isEmpty(),
                () -> assertThat(requestLine.getVersion()).isEqualTo(HttpVersion.HTTP_1_1)
        );
    }

    @DisplayName("여러 쿼리 파라미터를 파싱할 수 있다.")
    @Test
    void from_multipleQueryParams() {
        // given
        final String requestLineString = "POST /users?account=gugu&name=hkkang HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(requestLineString);

        // then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(requestLine.getPath()).isEqualTo("/users"),
                () -> assertThat(requestLine.getQueryParams()).hasSize(2),
                () -> assertThat(requestLine.getQueryParams().get("account")).containsExactly("gugu"),
                () -> assertThat(requestLine.getQueryParams().get("name")).containsExactly("hkkang")
        );
    }

    @DisplayName("중복된 키를 가진 쿼리 파라미터를 리스트로 파싱한다.")
    @Test
    void from_duplicateQueryParamKeys() {
        // given
        final String requestLineString = "GET /select?option=A&option=B&option=C HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(requestLineString);

        // then
        final List<String> options = requestLine.getQueryParams().get("option");
        assertThat(options).containsExactly("A", "B", "C");
    }

    @DisplayName("URL 인코딩된 쿼리 파라미터를 디코딩하여 파싱한다.")
    @Test
    void from_urlEncodedParams() {
        // given
        final String requestLineString = "GET /search?query=%ED%95%9C%EA%B8%80&sort=desc HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(requestLineString);

        // then
        assertAll(
                () -> assertThat(requestLine.getQueryParams().get("query")).containsExactly("한글"),
                () -> assertThat(requestLine.getQueryParams().get("sort")).containsExactly("desc")
        );
    }

    @DisplayName("비어있거나 null인 요청 라인은 INVALID 객체를 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void from_invalidLine(final String invalidRequestLineString) {
        // when
        final RequestLine requestLine = RequestLine.from(invalidRequestLineString);
        final RequestLine invalid = RequestLine.createInvalid();

        // then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(invalid.getMethod()),
                () -> assertThat(requestLine.getPath()).isEqualTo(invalid.getPath()),
                () -> assertThat(requestLine.getQueryParams()).isEqualTo(invalid.getQueryParams()),
                () -> assertThat(requestLine.getVersion()).isEqualTo(invalid.getVersion())
        );
    }
}
