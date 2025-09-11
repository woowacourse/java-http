package com.techcourse.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http.request.RequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @DisplayName("request body 생성")
    @Test
    void fromTest1() {
        // given
        String requestBodyString = "account=testuser&password=testpass&email=test@example.com";

        // when
        RequestBody requestBody = RequestBody.from(requestBodyString);

        // then
        assertThat(requestBody.values()).hasSize(3);
        assertThat(requestBody.values().get("account")).isEqualTo("testuser");
        assertThat(requestBody.values().get("password")).isEqualTo("testpass");
        assertThat(requestBody.values().get("email")).isEqualTo("test@example.com");
    }

    @DisplayName("잘못된 형식인 경우")
    @Test
    void fromTest2() {
        // given
        String invalidRequestBodyString = "invalid-format";

        // when & then
        assertThatThrownBy(() -> RequestBody.from(invalidRequestBodyString))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("request body의 형식은 'key=value' 이여야 합니다.");
    }

    @DisplayName("빈 request body 생성")
    @Test
    void empty() {
        // when
        RequestBody requestBody = RequestBody.empty();

        // then
        assertThat(requestBody.values()).isEmpty();
    }
}
