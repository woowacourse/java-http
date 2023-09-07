package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    @DisplayName("빈 RequestBody 를 만든다.")
    void empty() {
        // given
        // when
        final RequestBody requestBody = RequestBody.empty();

        // then
        assertThat(requestBody.getFieldsWithValue()).isEmpty();
    }

    @Test
    @DisplayName("Form 타입의 RequestBody 문자열로부터 데이터를 파싱한다.")
    void from_formData() {
        // given
        final String bodyMessage = "account=gugu&password=password";

        // when
        final RequestBody requestBody = RequestBody.from(bodyMessage);

        // then
        assertThat(requestBody.getFieldsWithValue()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                "account", "gugu",
                "password", "password"
            )
        );
    }
}
