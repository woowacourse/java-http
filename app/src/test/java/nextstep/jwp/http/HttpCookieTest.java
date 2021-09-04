package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    private HttpCookie httpCookie;

    @BeforeEach
    void setUp() {
        String cookieData = "fortune=yoonsung; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46; hey=man";
        httpCookie = new HttpCookie(cookieData);
    }

    @DisplayName("쿠키에 해당하는 key값이 있으면, true를 반환한다.")
    @Test
    void containsKey() {
        assertThat(httpCookie.containsKey("JSESSIONID")).isTrue();
        assertThat(httpCookie.containsKey("papi")).isFalse();
    }

    @DisplayName("쿠키에 key값을 보내면, key값에 맞는 value를 반환한다.")
    @Test
    void getCookieValueByKey() {
        assertThat(httpCookie.getCookieValueByKey("fortune")).isEqualTo("yoonsung");
        assertThat(httpCookie.getCookieValueByKey("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @DisplayName("쿠키에 없는 키값을 통해서 데이터를 얻으려고하면, 예외가 발생한다.")
    @Test
    void getCookieValueByKeyExceptionTest() {
        assertThatThrownBy(() -> {
            httpCookie.getCookieValueByKey("hahahaahaha");
        }).isInstanceOf(IllegalArgumentException.class);
    }
}