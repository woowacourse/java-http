package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyHttpResponseTest {

    @Test
    void 같은_이름의_헤더를_추가하면_추가한_순서대로_각각_직렬화한다() {
        // given
        MyHttpResponse response = new MyHttpResponse();
        response.setStatusCode(StatusCode.OK);
        response.addHeader("Set-Cookie", "SESSION=one");
        response.addHeader("Set-Cookie", "SESSION=two");

        // when
        String result = response.build();

        // then
        assertThat(result).containsSubsequence(
                "Set-Cookie: SESSION=one \r\n",
                "Set-Cookie: SESSION=two \r\n"
        );
    }

    @Test
    void setHeader는_같은_이름의_기존_값을_교체한다() {
        // given
        MyHttpResponse response = new MyHttpResponse();
        response.setStatusCode(StatusCode.OK);
        response.setHeader("X-Test", "old");

        // when
        response.setHeader("X-Test", "new");
        String result = response.build();

        // then
        assertThat(result)
                .contains("X-Test: new \r\n")
                .doesNotContain("X-Test: old \r\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Content-Length", "Content-Type", "Location"})
    void 단일_값_헤더는_addHeader로_추가할_수_없다(String headerName) {
        // given
        MyHttpResponse response = new MyHttpResponse();

        // when & then
        assertThatThrownBy(() -> response.addHeader(headerName, "value"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
