package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.request.StartLine;
import org.junit.jupiter.api.Test;

class StartLineTest {

    @Test
    void createStartLine() {
        final StartLine startLine = StartLine.from("GET /index.html HTTP/1.1");

        assertThat(startLine.isGet());
    }

    @Test
    void cannotCreateStartLine() {
        assertThatThrownBy(() -> StartLine.from("GET HTTP/1.1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청 첫 줄에 비어있는 값이 있습니다.");
    }
}
