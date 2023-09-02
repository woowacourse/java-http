package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpHeadersTest {

    @Test
    void addHeader는_기존의_값에_추가된다() {
        // given
        HttpHeaders httpHeaders = new HttpHeaders();

        // when
        httpHeaders.addHeader("key", "foo");
        httpHeaders.addHeader("key", "bar");

        // then
        assertThat(httpHeaders.getHeader("key")).isEqualTo("foo,bar");
    }

    @Test
    void setHeader는_기존의_값을_덮어쓴다() {
        // given
        HttpHeaders httpHeaders = new HttpHeaders();

        // when
        httpHeaders.setHeader("key", "foo");
        httpHeaders.setHeader("key", "bar");

        // then
        assertThat(httpHeaders.getHeader("key")).isEqualTo("bar");
    }

    @Test
    void getHeader_호출시_헤더가_없으면_blank() {
        // given
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setHeader("key", "foo");

        // when
        String header = httpHeaders.getHeader("bar");

        // then
        assertThat(header).isBlank();
    }
}
