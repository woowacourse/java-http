package org.apache.coyote.http11.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SetCookieStringBuilderTest {

    @Test
    @DisplayName("옵션에 맞는 Set-Cookie에 들어갈 헤더 밸류를 생성한다.")
    void build() {
        final SetCookieStringBuilder setCookieStringBuilder
                = new SetCookieStringBuilder(new Cookie("testKey", "testValue"));
        setCookieStringBuilder.setMaxAge(1800);
        setCookieStringBuilder.setDomain("http://localhost:8080");
        setCookieStringBuilder.setPath("/");
        setCookieStringBuilder.setSecure(true);
        setCookieStringBuilder.setHttpOnly(true);

        final String expected = "testKey=testValue; "
                + "Max-Age=1800; "
                + "Domain=http://localhost:8080; "
                + "Path=/; "
                + "Secure; "
                + "HttpOnly";

        final String actual = setCookieStringBuilder.toString();

        assertThat(actual).isEqualTo(expected);
    }
}