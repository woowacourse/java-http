package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import org.apache.coyote.http11.common.Headers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RequestHeaders 클래스의")
class HeadersTest {

    @Nested
    @DisplayName("add 메서드는")
    class Add {

        @Test
        @DisplayName("헤더 필드를 추가한다.")
        void success() {
            // given
            final String headerField = "Host: localhost:8080";
            final Headers headers = new Headers();

            // when
            headers.add(headerField);

            // then
            assertThat(headers.getValues()).contains(entry("Host", "localhost:8080"));
        }

        @Test
        @DisplayName("헤더 필드가 올바른 형식이 아닌 경우 예외를 던진다.")
        void invalidHeaderField_ExceptionThrown() {
            // given
            final String headerField = "Connection= keep-alive";
            final Headers headers = new Headers();

            // when & then
            assertThatThrownBy(() -> headers.add(headerField))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 Header Field 형식이 아닙니다.");
        }
    }
}
