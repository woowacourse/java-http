package org.springframework.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.Cookie.CookieBuilder;

class CookieTest {

    @DisplayName("쿠키 빌더는 name, value 중 하나라도 null 또는 빈 문자열일 경우 예외를 던진다")
    @Test
    void builder_throws_exception_when_name_or_value_is_null_or_empty() {
        // given
        final CookieBuilder cookieBuilder = Cookie
                .builder()
                .name("name")
                .value(" ");

        final CookieBuilder cookieBuilder2 = Cookie
                .builder()
                .name(" ")
                .value("value");

        final CookieBuilder cookieBuilder3 = Cookie
                .builder()
                .name("name")
                .value(null);

        final CookieBuilder cookieBuilder4 = Cookie
                .builder()
                .name(null)
                .value("value");

        // when & then
        assertAll(
                () -> assertThatThrownBy(cookieBuilder::build).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(cookieBuilder2::build).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(cookieBuilder3::build).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(cookieBuilder4::build).isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("유효한 쿠키 이름과 값을 전달하여 쿠키를 생성할 수 있다")
    @Test
    void create_cookie() {
        // given
        final String expectedName = "JSESSIONID";
        final String expectedValue = UUID.randomUUID().toString();

        // when
        final Cookie actual = Cookie.builder()
                .name(expectedName)
                .value(expectedValue)
                .build();

        // then
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(expectedName),
                () -> assertThat(actual.getValue()).isEqualTo(expectedValue)
        );
    }

    @DisplayName("getCookieString 메서드는 쿠키의 이름과 값을 = 로 연결하여 반환한다")
    @Test
    void getCookieString() {
        // given
        final String name = "JSESSIONID";
        final String value = UUID.randomUUID().toString();
        final Cookie cookie = Cookie.builder()
                .name(name)
                .value(value)
                .build();
        final String expected = String.join("=", name, value);

        // when
        final String actual = cookie.getCookieString();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
