package common.http;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CookiesTest {

    @Test
    void 세션_아이디로_쿠키를_생성한다() {
        // given
        String id = "sessionid";

        // when
        Cookie cookie = Cookies.ofJSessionId(id);

        // then
        assertThat(cookie.getAttribute("JSESSIONID")).isEqualTo(id);
    }
}
