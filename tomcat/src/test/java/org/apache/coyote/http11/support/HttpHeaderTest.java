package org.apache.coyote.http11.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpHeaderTest {

    @DisplayName("헤더 객체를 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "Content-Type", "Content-Length", "Location",
            "Host", "Connection", "Accept", "Accept-Encoding",
            "Accept-Language", "Cookie", "Set-Cookie"})
    void from(final String header) {
        // given, when, then
        assertThatCode(() -> HttpHeader.from(header))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하는 헤더인지 확인한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "Content-Type", "Content-Length", "Location",
            "Host", "Connection", "Accept", "Accept-Encoding",
            "Accept-Language", "Cookie", "Set-Cookie"})
    void contains(final String header) {
        // given, when, then
        assertThat(HttpHeader.contains(header)).isTrue();
    }

    @DisplayName("존재하지 않는 헤더라면 false를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "some", "Not-Header"})
    void contains_returnsFalse(final String header) {
        // given, when, then
        assertThat(HttpHeader.contains(header)).isFalse();
    }
}
