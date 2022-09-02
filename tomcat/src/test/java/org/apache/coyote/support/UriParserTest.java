package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.coyote.exception.InvalidHttpRequestFormatException;
import org.junit.jupiter.api.Test;

class UriParserTest {

    @Test
    void 스트림의_첫줄을_읽어서_URI로_파싱_테스트() throws IOException {
        // given
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream("GET /url HTTP/1.1 ".getBytes())));

        // when
        String actual = UriParser.parseUri(bufferedReader);

        // then
        assertThat(actual).isEqualTo("/url");
    }

    @Test
    void 스트림_첫줄의_형식이_올바르지_않으면_예외를_반환한다() {
        // given
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream("invalid".getBytes())));

        // when, then
        assertThatThrownBy(() -> UriParser.parseUri(bufferedReader))
                .isExactlyInstanceOf(InvalidHttpRequestFormatException.class);
    }

    @Test
    void URI에서_쿼리스트링_분리하고_URL_반환_테스트() {
        // given
        String uri = "/uri?query=a";

        // when
        String url = UriParser.parseUrl(uri);

        // then
        assertThat(url).isEqualTo("/uri");
    }

    @Test
    void URL_파싱_시_쿼리스트링이_없으면_URI를_그대로_반환한다() {
        // given
        String uri = "/uri";

        // when
        String url = UriParser.parseUrl(uri);

        // then
        assertThat(url).isEqualTo("/uri");
    }

    @Test
    void URI에서_쿼리스트링_추출_테스트() {
        // given
        String uri = "/uri?query=a";

        // when
        String queryString = UriParser.parseQueryString(uri);

        // then
        assertThat(queryString).isEqualTo("query=a");
    }

    @Test
    void URI에_쿼리스트링이_없으면_쿼리스트링_추출_시_빈_쿼리스트링을_반환한다() {
        // given
        String uri = "/uri";

        // when
        String queryString = UriParser.parseQueryString(uri);

        // then
        assertThat(queryString).isEqualTo("");
    }

    @Test
    void 확장자_가져오기_테스트() {
        // given
        String fileName = "nextstep.txt";

        // when
        String actual = UriParser.parseExtension(fileName);

        // then
        assertThat(actual).isEqualTo("txt");
    }

    @Test
    void 확장자가_없으면_빈_문자열을_반환한다() {
        // given
        String fileName = "no_extension";

        // when
        String actual = UriParser.parseExtension(fileName);

        // then
        assertThat(actual).isBlank();
    }
}
