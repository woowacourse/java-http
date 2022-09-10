package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import java.util.Map;
import org.apache.coyote.http11.common.Headers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RequestBody 클래스의")
class RequestBodyTest {

    @Nested
    @DisplayName("parseApplicationFormData 메서드는")
    class ParseApplicationFormData {

        @Test
        @DisplayName("application/x-www-form-urlencoded 형식에 따라 파싱한 데이터 결과를 Map으로 반환한다.")
        void success() {
            // given
            final String requestBodyAsString = "account=gugu&password=password&email=hkkang%40woowahan.com";
            final RequestBody requestBody = new RequestBody(requestBodyAsString);

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
            final RequestBody requestBody = new RequestBody(requestBodyAsString);

            // when & then
            assertThatThrownBy(requestBody::parseApplicationFormData)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 application/x-www-form-urlencoded 형식이 아닙니다.");
        }
    }
}
