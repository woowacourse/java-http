package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @Test
    @DisplayName("문자열과 맞는 헤더를 찾아서 반환한다.")
    void of() {
        final HttpHeader connection = HttpHeader.of("Connection");

        assertThat(connection.getValue()).isEqualTo("Connection");
    }

    @Test
    @DisplayName("문자열과 맞는 헤더가 없다면 기본 헤더가 반환한다.")
    void ofWithWrongValue() {
        final HttpHeader connection = HttpHeader.of("Connection2");

        assertThat(connection.getValue()).isEqualTo("");
    }
}
