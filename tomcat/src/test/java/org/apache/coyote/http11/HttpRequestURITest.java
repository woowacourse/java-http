package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestURITest {

    @DisplayName("URI 문자열을 입력 받아 객체를 생성한다. (쿼리 스트링이 존재할 때)")
    @Test
    void from_QueryStringExist() {
        // given
        final String rawUri = "/login?account=gugu&password=password";

        // when
        final HttpRequestURI httpRequestURI = HttpRequestURI.from(rawUri);

        // then
        assertAll(
            () -> assertThat(httpRequestURI.getPath()).isEqualTo("/login"),
            () -> assertThat(httpRequestURI.hasQueryString()).isTrue(),
            () -> assertThat(httpRequestURI.getQueryStrings().getValueByName("account")).isEqualTo("gugu"),
            () -> assertThat(httpRequestURI.getQueryStrings().getValueByName("password")).isEqualTo("password")
        );
    }

    @DisplayName("URI 문자열을 입력 받아 객체를 생성한다. (쿼리 스트링이 존재할 때)")
    @Test
    void from_NotExistQueryString() {
        // given
        final String rawUri = "/";

        // when
        final HttpRequestURI httpRequestURI = HttpRequestURI.from(rawUri);

        // then
        assertAll(
            () -> assertThat(httpRequestURI.getPath()).isEqualTo("/index.html"),
            () -> assertThat(httpRequestURI.hasSamePath("/index.html")).isTrue(),
            () -> assertThat(httpRequestURI.hasQueryString()).isFalse()
        );
    }
}
