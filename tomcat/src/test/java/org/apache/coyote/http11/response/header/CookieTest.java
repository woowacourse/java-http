package org.apache.coyote.http11.response.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CookieTest {

    @DisplayName("name에 해당하는 Cookie가 존재하는지 반환한다.")
    @ParameterizedTest
    @CsvSource({"JSESSIONID, true", "KKKKSESSIONID, false"})
    void containsCookieOf(String cookieName, boolean expected) {
        Cookie cookie = Cookie.fromRequest("JSESSIONID=asdafasdasdasfasf; TMPSESSIONID=asdasdasadas");

        boolean actual = cookie.containsCookieOf(cookieName);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("쿠키를 헤더포맷으로 반환한다.")
    @Test
    void toHeaderFormat() {
        String jSessionIdValue = UUID.randomUUID()
                .toString();
        Cookie cookie = Cookie.fromResponse("JSESSIONID", jSessionIdValue);

        boolean contains = cookie.containsCookieOf("JSESSIONID");
        String headerFormat = cookie.toHeaderFormat();

        assertAll(
                () -> assertThat(contains).isTrue(),
                () -> assertThat(headerFormat).contains("Set-Cookie: JSESSIONID=" + jSessionIdValue)
        );
    }
}
