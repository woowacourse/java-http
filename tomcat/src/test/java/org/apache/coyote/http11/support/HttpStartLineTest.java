package org.apache.coyote.http11.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import org.apache.coyote.http11.web.QueryParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Collections;

class HttpStartLineTest {

    @DisplayName("HttpStartLine을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/login?name=sun"})
    void from() {
        // given
        final String[] request = {"GET", "/index.html", "HTTP/1.1"};

        // when, then
        assertThatCode(() -> HttpStartLine.from(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("HttpStartLine의 길이에 맞지 않을 경우 예외가 발생한다.")
    @Test
    void from_throwsException_ifInvalidSize() {
        // given
        final String[] request = {"GET", "/index.html"};

        // when, then
        assertThatCode(() -> HttpStartLine.from(request))
                .isInstanceOf(InvalidHttpRequestException.class);
    }

    @DisplayName("QueryParameter가 존재하지 않는다면 emptyMap을 반환한다.")
    @Test
    void getQueryParameters_returnsEmptyMap() {
        // given
        final String[] request = {"GET", "/index.html", "HTTP/1.1"};
        final HttpStartLine httpStartLine = HttpStartLine.from(request);

        // when, then
        assertThat(httpStartLine.getQueryParameters()).isEqualTo(new QueryParameters(Collections.emptyMap()));
    }
}
