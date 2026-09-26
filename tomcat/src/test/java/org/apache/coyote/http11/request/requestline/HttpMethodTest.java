package org.apache.coyote.http11.request.requestline;

import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.exception.NotImplementedException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HttpMethodTest {
    @Nested
    class 지원하는_메서드 {

        @ParameterizedTest
        @EnumSource(HttpMethod.class)
        void 이름과_정확히_일치하면_해당_메서드를_반환한다(final HttpMethod expected) {
            assertThat(HttpMethod.from(expected.name())).isEqualTo(expected);
        }

        @Test
        void HEAD를_지원한다() {
            assertThat(HttpMethod.from("HEAD")).isEqualTo(HttpMethod.HEAD);
        }
    }

    @Nested
    class 형식이_잘못된_메서드는_400 {

        @ParameterizedTest
        @NullAndEmptySource
        void null이나_빈_문자열(final String token) {
            assertThatThrownBy(() -> HttpMethod.from(token))
                    .isInstanceOf(BadRequestException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "G ET",       // 공백
                "GET\t",      // 탭
                "GET\r",      // CR
                "GET\0",      // null
                "GET(",       // 괄호는 구분자
                "GET/",       // 슬래시는 구분자
                "\"GET\"",    // 따옴표는 구분자
                "GET:",       // 콜론은 구분자
                "게트",        // 비ASCII
                "ＧＥＴ",      // 전각 문자
        })
        void token에_허용되지_않는_문자가_포함된_경우(final String token) {
            assertThatThrownBy(() -> HttpMethod.from(token))
                    .isInstanceOf(BadRequestException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "@",   // 'A' 바로 앞 (0x40)
                "[",   // 'Z' 바로 뒤 (0x5B)
                "{",   // 'z' 바로 뒤 (0x7B)
                "/",   // '0' 바로 앞 (0x2F)
                ":",   // '9' 바로 뒤 (0x3A)
        })
        void 문자_범위의_경계_바로_바깥(final String token) {
            assertThatThrownBy(() -> HttpMethod.from(token))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @Nested
    class 형식은_맞지만_지원하지_않는_메서드는_501 {

        @ParameterizedTest
        @ValueSource(strings = {"PATCH", "OPTIONS", "TRACE", "CONNECT", "PROPFIND", "M-SEARCH"})
        void 표준이나_확장_메서드(final String token) {
            assertThatThrownBy(() -> HttpMethod.from(token))
                    .isInstanceOf(NotImplementedException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"get", "Get", "gET", "post"})
        void 대소문자가_다르면_다른_메서드다(final String token) {
            assertThatThrownBy(() -> HttpMethod.from(token))
                    .isInstanceOf(NotImplementedException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"GTE", "X_CUSTOM", "A", "Z", "a", "z", "0", "9", "!#$%&'*+-.^_`|~"})
        void 존재하지_않지만_token_규칙에_맞는_경우(final String token) {
            assertThatThrownBy(() -> HttpMethod.from(token))
                    .isInstanceOf(NotImplementedException.class);
        }
    }
}