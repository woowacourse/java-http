package org.apache.coyote.http11.response.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CookiesTest {

    @DisplayName("name에 해당하는 Cookie가 존재하는지 반환한다.")
    @ParameterizedTest
    @CsvSource({"JSESSIONID, true", "KKKKSESSIONID, false"})
    void containsCookieOf(String cookieName, boolean expected) {
        Cookies cookies = Cookies.fromRequest("JSESSIONID=asdafasdasdasfasf; TMPSESSIONID=asdasdasadas");

        boolean actual = cookies.containsCookieOf(cookieName);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("쿠키를 헤더포맷으로 반환한다.")
    @Test
    void toHeaderFormat() {
        Cookies cookies = Cookies.fromResponse("JSESSIONID");

        boolean contains = cookies.containsCookieOf("JSESSIONID");
        String headerFormat = cookies.toHeaderFormat();

        assertAll(
                () -> assertThat(contains).isTrue(),
                () -> assertThat(headerFormat).contains("Set-Cookie: JSESSIONID=")
        );
    }
}
