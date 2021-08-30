package nextstep.jwp.framework.http.formatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;
import nextstep.jwp.framework.http.HttpVersion;
import nextstep.jwp.framework.http.StatusLine;

import static org.assertj.core.api.Assertions.assertThat;

class StatusLineFormatterTest {

    @DisplayName("StatusLine을 HTTP 형식의 String 으로 변환 테스트")
    @Test
    void transformTest() {

        // given
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);
        final HttpResponse httpResponse = new HttpResponse.Builder().statusLine(statusLine).build();
        final StatusLineFormatter statusLineFormatter = new StatusLineFormatter(httpResponse);

        // when
        final String httpStatusLine = statusLineFormatter.transform();

        // then
        assertThat(httpStatusLine).isEqualTo(StatusLineFormatter.STATUS_LINE_FORMAT,
                statusLine.getHttpVersion(), statusLine.getHttpStatusCode(), statusLine.getReasonPhrase());
    }
}
