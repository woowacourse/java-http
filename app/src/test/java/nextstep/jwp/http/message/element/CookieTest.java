package nextstep.jwp.http.message.element;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CookieTest {

    @Test
    void get() {
    }

    @DisplayName("쿠키가 존재하면 쿠키를 추출한다.")
    @Test
    void get_exist() {
        Cookie cookie = new Cookie("test1=1; test2=2");

        assertThat(cookie.get("test1")).get().isEqualTo("1");
        assertThat(cookie.get("test2")).get().isEqualTo("2");
    }

    @Test
    void size() {
        Cookie cookie = new Cookie("test1=1; test2=2");

        assertThat(cookie.size()).isEqualTo(2);
    }
}
