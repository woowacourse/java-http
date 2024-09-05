package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BodyTest {

    @DisplayName("application/x-www-form-urlencoded형의 body를 파싱한다.")
    @Test
    void parseRequestBody() {
        // given
        Map<String, String> expected = Map.of("account", "validUser", "password", "wrongPassword");
        Body body = new Body("account=validUser&password=wrongPassword");

        // when
        Map<String, String> result = body.parseRequestBody();

        // then
        assertThat(result).containsAllEntriesOf(expected);
    }
}
