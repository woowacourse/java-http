package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.StringJoiner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseBodyTest {

    @DisplayName("유효한 파일 경로가 입력된 경우 해당 파일의 내용을 응답 바디에 저장한다.")
    @Test
    void should_addFileAtBody() throws URISyntaxException, IOException {
        // given
        ResponseBody body = new ResponseBody();

        // when
        body.addFile("/default.html");

        // then
        StringJoiner joiner = new StringJoiner("");
        body.buildHttpMessage(joiner);
        assertThat(joiner.toString()).isEqualTo("Hello world!");
    }

    @DisplayName("유효하지 않은 파일 경로가 입력된 경우 빈 문자열을 응답 바디에 저장한다.")
    @Test
    void should_addEmptyBody_when_invalidFile() throws URISyntaxException, IOException {
        // given
        ResponseBody body = new ResponseBody();

        // when
        body.addFile("/invalid.html");

        // then
        StringJoiner joiner = new StringJoiner("");
        body.buildHttpMessage(joiner);
        assertThat(joiner.toString()).isEqualTo("");
    }

    @DisplayName("바디에 값을 저장한 경우 해당 바디의 길이를 반환한다.")
    @Test
    void should_getLengthOfBody() throws URISyntaxException, IOException {
        // given
        ResponseBody body = new ResponseBody();
        body.addFile("/default.html");

        // when & then
        assertThat(body.getLength()).isEqualTo("Hello world!".length());
    }

    @DisplayName("바디에 값을 저장하지 않은 경우 길이가 0이다.")
    @Test
    void should_getLengthOfBody_when_emptyBody() {
        // given
        ResponseBody body = new ResponseBody();

        // when & then
        assertThat(body.getLength()).isZero();
    }
}
