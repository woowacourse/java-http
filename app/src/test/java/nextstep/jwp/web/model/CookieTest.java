package nextstep.jwp.web.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CookieTest {

    @DisplayName("객체 생성: 쿠키에 이름과 값은 null일 수 없습니다.")
    @Test
    void create() {
        // when then
        assertThatThrownBy(() -> new Cookie(null, "1"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(Cookie.NAME_NULL_EXCEPTION);
        assertThatThrownBy(() -> new Cookie("name", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(Cookie.VALUE_NULL_EXCEPTION);
    }

    @DisplayName("toSetMessage(): 쿠키를 세팅하기 위한 이름과 값을 하나의 문자열로 반환한다.")
    @Test
    void toSetMessage() {
        // given
        Cookie cookie = new Cookie("name", "value");

        // when
        String result = cookie.toSetMessage();
        String expected = "name=value";

        // then
        assertThat(result).isEqualTo(expected);
    }
}