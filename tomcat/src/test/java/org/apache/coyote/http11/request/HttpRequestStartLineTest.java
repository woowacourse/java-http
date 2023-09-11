package org.apache.coyote.http11.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestStartLineTest {

    @ParameterizedTest
    @CsvSource(value = {"GET / HTTP/1.1:false", "POST / HTTP1.1:true"}, delimiter = ':')
    @DisplayName("요청에 대해 isPost()를 호출하면 POST 요청인지 여부를 반환한다.")
    void isPost(final String startLine, final boolean expected) {
        //given
        final HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);

        //when
        final boolean actual = httpRequestStartLine.isPOST();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"GET / HTTP/1.1:true", "POST / HTTP1.1:false"}, delimiter = ':')
    @DisplayName("요청에 대해 isGet()를 호출하면 POST 요청인지 여부를 반환한다.")
    void isGET(final String startLine, final boolean expected) {
        //given
        final HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);

        //when
        final boolean actual = httpRequestStartLine.isGET();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET /login HTTP/1.1", "GET /login?a=b HTTP1.1"})
    @DisplayName("요청에 대해 isSamePath()에 '/login'을 입력하면 요청의 true를 반환한다.")
    void isSamePath(final String startLine) {
        //given
        final HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);

        //when
        final boolean actual = httpRequestStartLine.isSamePath("/login");

        //then
        Assertions.assertThat(actual).isTrue();
    }

}
