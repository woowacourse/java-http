package nextstep.jwp.http.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.InvalidRequestHeader;
import nextstep.jwp.exception.QueryParameterNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpCookieTest {

    @DisplayName("Cookie를 생성할 때 유효하지 않은 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1234", "a=", "A=1; b", "A=1; B="})
    void parseException(String cookieString) {
        assertThatThrownBy(() -> HttpCookie.parse(cookieString))
            .isExactlyInstanceOf(InvalidRequestHeader.class);
    }

    @DisplayName("Cookie에서 Parameter를 조회할 때")
    @Nested
    class getParameter {

        @DisplayName("해당 Parameter가 있으면 조회에 성공한다.")
        @Test
        void getParameter() {
            // given
            String parameterKey = "JSESSION";
            String parameterValue = "1234";
            String cookieString = String.format("%s=%s;", parameterKey, parameterValue);

            // when
            HttpCookie httpCookie = HttpCookie.parse(cookieString);
            String foundCookieValue = httpCookie.getParameter(parameterKey);

            // then
            assertThat(foundCookieValue).isEqualTo(parameterValue);
        }

        @DisplayName("해당 Parameter가 없으면 예외가 발생한다.")
        @Test
        void getParameterException() {
            // given
            HttpCookie httpCookie = HttpCookie.parse("JSESSION=1234;");

            // when, then
            assertThatThrownBy(() -> httpCookie.getParameter("none"))
                .isExactlyInstanceOf(QueryParameterNotFoundException.class);
        }
    }
}