package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.request.RequestHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeaderTest {

    @DisplayName("해당 key에 mapping되는 value를 가져올 수 있다.")
    @Test
    void getHeaderValue() {
        // given
        final String key = "key";
        final String expected = "value";
        final RequestHeader requestHeader = new RequestHeader(Map.of(
                key, expected,
                "two", "2",
                "three", "3")
        );

        // when
        final String actual = requestHeader.geHeaderValue(key);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("해당 key에 mapping되는 value가 없으면 빈 문자열을 반환한다.")
    @Test
    void getHeaderValue_nullKey() {
        // given
        final String key = "key";
        final String expected = "";
        final RequestHeader requestHeader = new RequestHeader(Map.of(
                "one", "1",
                "two", "2",
                "three", "3")
        );

        // when
        final String actual = requestHeader.geHeaderValue(key);

        // then
        assertThat(actual).isEqualTo(expected);
    }

}
