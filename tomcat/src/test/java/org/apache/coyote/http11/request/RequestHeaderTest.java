package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestHeaderTest {

    @Test
    void header_문자열을_입력받아_RequestHeader를_반환한다() {
        // given
        final String header = "Connection: keep-alive\r\nSec-Fetch-Dest: image\r\n";

        // when
        final RequestHeader requestHeader = RequestHeader.from(header);

        // then
        assertThat(requestHeader.getItems()).contains(
                entry("Connection", "keep-alive"),
                entry("Sec-Fetch-Dest", "image")
        );
    }
}
