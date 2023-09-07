package nextstep.org.apache.catalina.servlet.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.servlet.session.Cookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("Cookies 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CookiesTest {

    @Test
    void 쿠키_정보를_통해_생성된다() {
        // when
        Cookies from = Cookies.from(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        // then
        assertThat(from.get("yummy_cookie")).isEqualTo("choco");
        assertThat(from.get("tasty_cookie")).isEqualTo("strawberry");
        assertThat(from.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
