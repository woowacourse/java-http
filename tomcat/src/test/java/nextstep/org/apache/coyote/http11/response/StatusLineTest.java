package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("StatusLine 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StatusLineTest {

    @Test
    void statusLine을_생성한다() {
        // given
        StatusLine statusLine = new StatusLine(HttpStatus.OK);

        // when
        String actual = statusLine.toString();

        // then
        assertThat(actual).isEqualTo("HTTP/1.1 200 OK");
    }
}
