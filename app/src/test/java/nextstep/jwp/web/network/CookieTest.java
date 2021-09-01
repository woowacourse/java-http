package nextstep.jwp.web.network;

import nextstep.jwp.web.network.request.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CookieTest {

    @DisplayName("key와 value를 String으로 받아 cookie 생성 - 성공")
    @Test
    void create() {
        // given
        final String string = "JSESSIONID=59941ed0-f444-4a31-b75d-4e4803686e01";

        // when // then
        assertThatCode(() -> new Cookie(string))
                .doesNotThrowAnyException();
    }

    @DisplayName("cookie에서 key와 value를 가져온다 - 성공")
    @Test
    void getKeyAndValue() {
        // given
        final String expectedKey = "JSESSIONID";
        final String expectedValue = "59941ed0-f444-4a31-b75d-4e4803686e01";
        final Cookie cookie = new Cookie(expectedKey + "=" + expectedValue);

        // when
        final String key = cookie.getKey();
        final String value = cookie.getValue();

        // then
        assertThat(key).isEqualTo(expectedKey);
        assertThat(value).isEqualTo(expectedValue);
    }
}