package nextstep.jwp.http.response;

import static nextstep.jwp.http.common.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.common.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusLineTest {

    @DisplayName("toString 요청시 문자열 형태로 반환받는다.")
    @Test
    void toStringTest() {
        // given
        StatusLine okStatusLine = StatusLine.from(OK);
        StatusLine noContentStatusLine = StatusLine.from(NO_CONTENT);
        StatusLine badRequestStatusLine = StatusLine.from(BAD_REQUEST);

        String expectOkStatusLineString = String.format("%s %s %s ",
                HttpVersion.HTTP_1_1.getValue(), OK.getCodeString(), OK.getReasonPhrase());
        String expectNoContentStatusLineString = String.format("%s %s %s ",
            HttpVersion.HTTP_1_1.getValue(), NO_CONTENT.getCodeString(), NO_CONTENT.getReasonPhrase());
        String expectBadRequestStatusLineString = String.format("%s %s %s ",
            HttpVersion.HTTP_1_1.getValue(), BAD_REQUEST.getCodeString(), BAD_REQUEST.getReasonPhrase());

        // when, then
        assertThat(okStatusLine.toString()).isEqualTo(expectOkStatusLineString);
        assertThat(noContentStatusLine.toString()).isEqualTo(expectNoContentStatusLineString);
        assertThat(badRequestStatusLine.toString()).isEqualTo(expectBadRequestStatusLineString);
    }
}