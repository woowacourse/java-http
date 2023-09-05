package org.apache.coyote.http11.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestCookiesTest {

    @Test
    void request_cookies_생성_테스트() {
        String jsessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        String cookies = "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=" + jsessionId;

        HttpRequestCookies httpRequestCookies = HttpRequestCookies.of(cookies);

        Assertions.assertThat(httpRequestCookies.get("JSESSIONID")).isEqualTo(jsessionId);
    }
}
