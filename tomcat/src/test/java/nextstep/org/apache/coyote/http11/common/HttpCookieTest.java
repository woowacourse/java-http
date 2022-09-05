package nextstep.org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.common.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("쿠키값이 없는 객체를 생성한다.")
    void empty() {
        //when
        HttpCookie cookie = HttpCookie.empty();

        //then
        String actual = cookie.toHeaderString();
        assertThat(actual).isEqualTo("");
    }

    @Test
    @DisplayName("입력값을 파싱하여 객체를 생성한다.")
    void createByParsing() {
        //given
        String input = "a=b; c=d";

        //when
        HttpCookie cookie = HttpCookie.createByParsing(input);

        //then
        String actual = cookie.toHeaderString();
        assertThat(actual).isEqualTo(input);
    }

    @Test
    @DisplayName("세션과 함께 객체를 생성한다.")
    void createWithSession() {
        //given
        String input = "sessionId";

        //when
        HttpCookie cookie = HttpCookie.createWithSession(input);

        //then
        String expected = "JSESSIONID=sessionId";
        String actual = cookie.toHeaderString();
        assertThat(actual).isEqualTo(expected);
    }
}
