package org.apache.coyote.http11.header;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeadersTest {

    @Test
    void 헤더_정보를_String으로_변환한다() {
        //given
        final Headers headers = new Headers();
        headers.addHeader(EntityHeader.CONTENT_TYPE, "text/html");
        headers.addHeader(EntityHeader.CONTENT_LENGTH, "100");

        //when
        final String actual = headers.parseResponse();

        //then
        final String expected = "Content-Type: text/html \r\n" +
                "Content-Length: 100 ";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 헤더_정보를_추가한다() {
        //given
        final Headers headers = new Headers();

        //when
        headers.addHeader(EntityHeader.CONTENT_TYPE, "text/html");

        //then
        assertThat(headers.getValue(EntityHeader.CONTENT_TYPE)).isEqualTo("text/html");
    }
}
