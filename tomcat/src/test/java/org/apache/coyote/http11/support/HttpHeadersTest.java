package org.apache.coyote.http11.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.LinkedHashMap;

class HttpHeadersTest {

    @DisplayName("Content-Length가 존재한다면 해당 길이를 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,1", "50,50"})
    void getContentLength(final String input, final String expected) {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.CONTENT_LENGTH, input);

        // when
        final String contentLength = httpHeaders.getContentLength();

        // then
        assertThat(contentLength).isEqualTo(expected);
    }


    @DisplayName("Content-Length가 존재하지 않는다면 0을 반환한다.")
    @Test
    void getContentLength_returnsDefaultValue() {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());

        // when
        final String contentLength = httpHeaders.getContentLength();
        final String expected = "0";

        //then
        assertThat(contentLength).isEqualTo(expected);
    }

    @DisplayName("HttpHeaders에 헤더를 추가한다.")
    @Test
    void put() {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());

        // when
        httpHeaders.put(HttpHeader.HOST, "localhost");

        // then
        assertThat(httpHeaders.getValues().size()).isOne();
    }
}
