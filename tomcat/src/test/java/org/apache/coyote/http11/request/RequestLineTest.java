package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestLineTest {

    @ParameterizedTest
    @ValueSource(strings = {"GET /login HTTP/1.1", "POST /login?account=gugu&password=password HTTP/1.1"})
    @DisplayName("MappingURI를 가공해서 반환한다.")
    void getMappingUri(final String startLine) {
        // given
        final RequestLine givenRequestLine = RequestLine.from(startLine);
        final String expected = "/login";

        // when
        String actual = givenRequestLine.getMappingUri();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
