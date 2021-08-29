package nextstep.jwp.http.message.element;

import nextstep.jwp.http.message.element.cookie.CookieImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CookieTest {

    @Test
    void get() {
    }

    @DisplayName("쿠키가 존재하면 쿠키를 추출한다.")
    @Test
    void get_exist() {
        CookieImpl cookie = new CookieImpl("test1=1; test2=2");

        assertThat(cookie.get("test1")).get().isEqualTo("1");
        assertThat(cookie.get("test2")).get().isEqualTo("2");
    }

    @Test
    void size() {
        CookieImpl cookie = new CookieImpl("test1=1; test2=2");

        assertThat(cookie.size()).isEqualTo(2);
    }
}
