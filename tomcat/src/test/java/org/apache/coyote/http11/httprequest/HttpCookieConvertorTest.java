package org.apache.coyote.http11.httprequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieConvertorTest {

    @DisplayName("HttpCookie를 정확히 추출한다")
    @Test
    void convertHttpCookie() {
        String rowCookie = "JSESSIONID=abcdefg; account=gugu; password=; uuid=mk";
        HttpCookie httpCookie = HttpCookieConvertor.convertHttpCookie(rowCookie);

        assertAll(
                () -> assertThat(httpCookie.getCookieValue("JSESSIONID")).isEqualTo("abcdefg"),
                () -> assertThat(httpCookie.getCookieValue("account")).isEqualTo("gugu"),
                () -> assertThat(httpCookie.getCookieValue("password")).isNull(),
                () -> assertThat(httpCookie.getCookieValue("uuid")).isEqualTo("mk")
        );
    }
}
