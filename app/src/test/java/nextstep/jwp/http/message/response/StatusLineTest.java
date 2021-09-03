package nextstep.jwp.http.message.response;

import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.common.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusLineTest {

    @DisplayName("StatusLine 을 생성하다.")
    @Test
    void create() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        HttpStatusCode httpStatusCode = HttpStatusCode.OK;

        //when
        StatusLine statusLine = new StatusLine(httpVersion, httpStatusCode);

        // then
        assertThat(statusLine.getHttpVersion()).isEqualTo(httpVersion);
        assertThat(statusLine.getHttpStatusCode()).isEqualTo(httpStatusCode);
    }

    @DisplayName("StatusLine 을 문자열로 반환한다.")
    @Test
    void asSting() {
        // given
        String expect = "HTTP/1.1 200 OK\r\n";
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatusCode.OK);

        // when, then
        assertThat(statusLine.asString()).isEqualTo(expect);
    }

    @DisplayName("StatusLine 을 바이트 배열로 변환할 때는 마지막에 개행 문자를 포함한다.")
    @Test
    void toBytes() {
        // given
        byte[] expect = "HTTP/1.1 200 OK\r\n".getBytes();
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatusCode.OK);

        // when, then
        assertThat(statusLine.toBytes()).isEqualTo(expect);
    }
}
