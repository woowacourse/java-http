package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StatusLineTest {

    @DisplayName("스테이터스 라인을 잘 생성하는지 확인한다.")
    @Test
    void getStatusLine() {
        // given
        final StatusLine statusLine = new StatusLine(HttpStatus.OK);

        // when
        final String actual = statusLine.getStatusLine();

        // then
        assertThat(actual).isEqualTo("HTTP/1.1 200 OK");
    }
}
