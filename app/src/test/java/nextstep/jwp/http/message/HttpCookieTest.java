package nextstep.jwp.http.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpCookieTest {

    @DisplayName("쿠키의 name은 null이거나 길이가 0일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void 쿠키_생성(String name) {
        // when, then
        assertThatThrownBy(() -> new HttpCookie(name, "binghe"))
            .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("쿠키 형식의 문자열을 쿠키로 생성할 수 있다.")
    @Test
    void parseFrom_문자열_쿠키_파싱() {
        // given
        String name = "JSESSIONID";
        String value = "656cef62-e3c4-40bc-a8df-94732920ed46";
        String cookie = String.join("=", name, value);

        // when
        HttpCookie parsedCookie = HttpCookie.parseFrom(cookie);

        // then
        assertThat(parsedCookie.getName()).isEqualTo(name);
        assertThat(parsedCookie.getValue()).isEqualTo(value);
    }

    @DisplayName("쿠키의 name이 null이거나 빈 문자열이면 예외가 발생한다.")
    @Test
    void parseFrom_잘못된_쿠키_형식_문자열_예외발생() {
        // given
        String cookie = "=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when, then
        assertThatThrownBy(() -> HttpCookie.parseFrom(cookie))
            .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("쿠키의 값을 문자열로 변환할 수 있다.")
    @Test
    void asString_쿠키_문자열로_변환() {
        // given
        String name = "JSESSIONID";
        String value = "656cef62-e3c4-40bc-a8df-94732920ed46";
        String cookie = String.join("=", name, value);

        // when
        HttpCookie parsedCookie = HttpCookie.parseFrom(cookie);

        // then
        assertThat(parsedCookie.asString()).isEqualTo(cookie);
    }
}
