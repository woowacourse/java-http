package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.coyote.http11.request.RequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {
    @DisplayName("application/x-www-form-urlencoded형의 body를 파싱한다.")
    @Test
    void parseRequestBody() {
        // given
        Map<String, String> expected = Map.of("account", "validUser", "password", "wrongPassword");
        RequestBody requestBody = new RequestBody("account=validUser&password=wrongPassword");

        // when
        Map<String, String> result = requestBody.parseRequestBody();

        // then
        assertThat(result).containsAllEntriesOf(expected);
    }
}
