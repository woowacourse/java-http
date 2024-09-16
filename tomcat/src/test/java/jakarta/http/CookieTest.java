package jakarta.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CookieTest {

    @Test
    @DisplayName("쿠키 문자열은 null이 될 수 있다.")
    void createWithNull() {
        assertThatCode(() -> new Cookie(null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("쿠키는 a=2 형식만 인식된다.")
    void singleFormat() {
        Cookie cookie = new Cookie("a:2");

        Optional<String> result = cookie.get("a");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("단일 쿠키를 조회한다.")
    void single() {
        Cookie cookie = new Cookie("a=2");

        Optional<String> result = cookie.get("a");

        assertThat(result).hasValue("2");
    }

    @Test
    @DisplayName("여러 쿠키값을 읽는다.")
    void multi() {
        Cookie cookie = new Cookie("a=1; b=2; c=3");

        Assertions.assertAll(
                () -> assertThat(cookie.get("a")).hasValue("1"),
                () -> assertThat(cookie.get("b")).hasValue("2"),
                () -> assertThat(cookie.get("c")).hasValue("3")
        );
    }
}
