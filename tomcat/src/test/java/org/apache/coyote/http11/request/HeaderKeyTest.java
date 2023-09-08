package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HeaderKeyTest {

    @Test
    @DisplayName("헤더값을 파싱할 수 있다")
    void from() {
        //given
        final String contentLength = "Content-Length";

        //when
        final HeaderKey header = HeaderKey.from(contentLength);

        //then
        assertThat(header).isEqualTo(HeaderKey.CONTENT_LENGTH);
    }

    @Test
    @DisplayName("없는 헤더값을 파싱하려고 하면 예외가 발생한다")
    void from_fail() {
        //given
        final String nothing = "Nothing";

        //when, then
        assertThatThrownBy(() -> HeaderKey.from(nothing))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("존재하는 헤더값인지 알 수 있다")
    void contains() {
        //given
        final String contentLength = "Content-Length";

        //when
        final boolean contains = HeaderKey.contains(contentLength);

        //then
        assertThat(contains).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 헤더값이라면 false를 리턴한다")
    void contains_false() {
        //given
        final String nothing = "Nothing";

        //when
        final boolean contains = HeaderKey.contains(nothing);

        //then
        assertThat(contains).isFalse();
    }
}
