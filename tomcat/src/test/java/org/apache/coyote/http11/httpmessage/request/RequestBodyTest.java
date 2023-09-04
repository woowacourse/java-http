package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    @DisplayName("문자열로부터 requestBody을 생성한다.")
    void make_request_body_from_string() {
        // given
        final String requestBody = "account=ako&password=password";
        // when
        final RequestBody result = RequestBody.fromRequest(requestBody);

        // then
        assertThat(result.getBody().size()).isEqualTo(2);
        assertThat(result.getBody().get("account")).isEqualTo("ako");
        assertThat(result.getBody().get("password")).isEqualTo("password");
    }
}
