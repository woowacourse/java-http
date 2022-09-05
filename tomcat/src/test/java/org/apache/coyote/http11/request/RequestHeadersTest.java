package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RequestHeaders 클래스의")
class RequestHeadersTest {

    @Nested
    @DisplayName("생성자는")
    class Constructor {

        @Test
        @DisplayName("BufferedReader를 사용하여 객체를 생성한다.")
        void success() {
            // given
            final String request = String.join("\r\n",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: */*",
                    "");
            final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // when
            final RequestHeaders requestHeaders = RequestHeaders.from(bufferedReader);

            // then
            assertThat(requestHeaders.getHeaders()).contains(
                    entry("Host", "localhost:8080"),
                    entry("Connection", "keep-alive"),
                    entry("Accept", "*/*")
            );
        }

        @Test
        @DisplayName("헤더 필드가 올바른 형식이 아닌 경우 예외를 던진다.")
        void invalidHeaderField_ExceptionThrown() {
            // given
            final String request = String.join("\r\n",
                    "Connection= keep-alive",
                    "");
            final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // when & then
            assertThatThrownBy(() -> RequestHeaders.from(bufferedReader))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 Header Field 형식이 아닙니다.");
        }
    }
}
