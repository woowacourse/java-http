package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("RequestBody 클래스의")
class RequestBodyTest {

    private RequestHeaders createRegisterRequestHeaders(final int contentLength) {
        final String request = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + contentLength,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "");
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return RequestHeaders.from(bufferedReader);
    }

    @Nested
    @DisplayName("생성자는")
    class Constructor {

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = {"account=gugu&password=password&email=hkkang%40woowahan.com"})
        @DisplayName("BufferedReader를 사용하여 객체를 생성한다.")
        void success(final String requestBodyAsString) {
            // given
            final RequestHeaders requestHeaders = createRegisterRequestHeaders(requestBodyAsString.length());
            final InputStream inputStream = new ByteArrayInputStream(requestBodyAsString.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // when
            final RequestBody requestBody = RequestBody.of(bufferedReader, requestHeaders);

            // then
            assertThat(requestBody.getContents()).isEqualTo(requestBodyAsString);
        }

        @Test
        @DisplayName("헤더 필드 Content-Length 값이 Request Body 길이보다 작은 경우 Content-Length 길이를 기준으로 저장한다.")
        void incorrectContentLength() {
            // given
            final RequestHeaders requestHeaders = createRegisterRequestHeaders(10);
            final String requestBodyAsString = "account=gugu&password=password&email=hkkang%40woowahan.com";
            final InputStream inputStream = new ByteArrayInputStream(requestBodyAsString.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // when
            final RequestBody requestBody = RequestBody.of(bufferedReader, requestHeaders);

            // then
            assertThat(requestBody.getContents()).isEqualTo(requestBodyAsString.substring(0, 10));
        }

    }

    @Nested
    @DisplayName("parseApplicationFormData 메서드는")
    class ParseApplicationFormData {

        @Test
        @DisplayName("application/x-www-form-urlencoded 형식에 따라 파싱한 데이터 결과를 Map으로 반환한다.")
        void success() {
            // given
            final String requestBodyAsString = "account=gugu&password=password&email=hkkang%40woowahan.com";
            final RequestHeaders requestHeaders = createRegisterRequestHeaders(requestBodyAsString.length());
            final InputStream inputStream = new ByteArrayInputStream(requestBodyAsString.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final RequestBody requestBody = RequestBody.of(bufferedReader, requestHeaders);

            // when
            final Map<String, String> params = requestBody.parseApplicationFormData();

            // then
            assertThat(params).contains(
                    entry("account", "gugu"),
                    entry("password", "password"),
                    entry("email", "hkkang%40woowahan.com")
            );
        }

        @Test
        @DisplayName("application/x-www-form-urlencoded 형식이 아닌 경우 예외를 던진다.")
        void invalidFormData_ExceptionThrown() {
            // given
            final String requestBodyAsString = "account:gugu/password:password/email:hkkang%40woowahan.com";
            final RequestHeaders requestHeaders = createRegisterRequestHeaders(requestBodyAsString.length());
            final InputStream inputStream = new ByteArrayInputStream(requestBodyAsString.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final RequestBody requestBody = RequestBody.of(bufferedReader, requestHeaders);

            // when & then
            assertThatThrownBy(requestBody::parseApplicationFormData)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 application/x-www-form-urlencoded 형식이 아닙니다.");
        }
    }
}
