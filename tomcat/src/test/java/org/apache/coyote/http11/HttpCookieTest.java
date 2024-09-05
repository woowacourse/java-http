package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("쿠키 문자열을 파싱하여 인스턴스 생성")
    @Test
    void construct_Success() {
        HttpCookie httpCookie = new HttpCookie("name1=value1; name2=value2");
        assertThat(httpCookie.getCookiesAsString())
                .isIn("name1=value1; name2=value2", "name2=value2; name1=value1");
    }

    @DisplayName("쿠키 정보 추가")
    @Test
    void add() {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.add("name1", "value1");
        httpCookie.add("name2", "value2");
        assertThat(httpCookie.getCookiesAsString())
                .isIn("name1=value1; name2=value2", "name2=value2; name1=value1");
    }
}
