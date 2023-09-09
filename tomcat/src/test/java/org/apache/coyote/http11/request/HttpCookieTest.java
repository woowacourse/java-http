package org.apache.coyote.http11.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpCookieTest {

    @ParameterizedTest
    @CsvSource(value = {"test=aaa:false", "JSESSION=abcdefg:true"}, delimiter = ':')
    @DisplayName("isExist()를 호출하면 쿠키의 존재 여부를 반환한다.")
    void isExist(final String rawCookie, final boolean expected) {
        //given
        final HttpCookie cookie = HttpCookie.from(rawCookie);

        //when
        final boolean actual = cookie.isExist("JSESSION");

        //then
        Assertions.assertThat(actual).isEqualTo(expected);

    }
}
