package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @DisplayName("bodyLine을 받아 RequestBody를 생성할 수 있다.")
    @Test
    void parse() {
        // given
        final String bodyLine = "one=1&two=2&three=3";

        final Map<String, String> expected = Map.of(
                "one", "1",
                "two", "2",
                "three", "3"
        );

        // when
        final Map<String, String> actual = RequestBody.parse(bodyLine).getBody();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("value가 없는 bodyLine으로 RequestBody를 생성할 수 있다.")
    @Test
    void parse_nullValue() {
        // given
        final String bodyLine = "one=&two=&three=";

        final Map<String, String> expected = Map.of(
                "one", "",
                "two", "",
                "three", ""
        );

        // when
        final Map<String, String> actual = RequestBody.parse(bodyLine).getBody();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
