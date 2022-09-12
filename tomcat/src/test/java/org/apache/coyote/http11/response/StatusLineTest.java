package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.TestRequest;

class StatusLineTest {

    @Test
    @DisplayName("HttpRequest와 StatusCode를 받아 StatusLine을 반환한다.")
    void generate() {
        // given
        final HttpRequest request = TestRequest.generateWithUri("/path");

        // when
        final StatusLine statusLine = new StatusLine(request, StatusCode.OK);
        final String actual = statusLine.generate();
        final String expected = "HTTP/1.1 200 OK ";

        // then
        assertThat(actual).isEqualTo(expected);
    }
}