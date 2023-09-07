package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Map;
import org.apache.coyote.http11.request.HttpCookie;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpCookieTest {

    @Test
    void Http_Cookie를_생성한다() {
        // given
        String httpCookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ";

        // when
        HttpCookie cookie = HttpCookie.from(httpCookie);

        // then
        Map<String, String> cookieParameters = cookie.parametersMap();
        assertSoftly(softly -> {
            softly.assertThat(cookieParameters.get("yummy_cookie")).isEqualTo("choco");
            softly.assertThat(cookieParameters.get("tasty_cookie")).isEqualTo("strawberry");
            softly.assertThat(cookieParameters.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        });
    }

    @Test
    void session_Id가_있는지_확인한다() {
        // given
        String httpCookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ";
        HttpCookie cookie = HttpCookie.from(httpCookie);

        // when
        boolean actual = cookie.containsCookie("JSESSIONID");

        // then
        assertThat(actual).isTrue();
    }
}
