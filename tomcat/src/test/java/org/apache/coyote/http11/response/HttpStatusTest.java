package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 상태")
class HttpStatusTest {

    @Test
    @DisplayName("상태 코드와 이유 문구를 HTTP 상태 형식으로 반환한다")
    void returnsCodeAndReasonPhrase() {
        // when
        final String actual = HttpStatus.BAD_REQUEST.toString();

        // then
        assertThat(actual).isEqualTo("400 Bad Request");
    }
}
