package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    @Test
    @DisplayName("Http Response Header를 Http Response용으로 가공해서 반환한다.")
    void getHeadersForResponse() {
        // given
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
        responseHeaders.addHeader(HttpHeaderName.CONTENT_LENGTH.getValue(), 124);

        String expected = String.join("\r\n",
                HttpHeaderName.CONTENT_TYPE.getValue() + ": " + ContentType.TEXT_HTML.getValue() + " ",
                HttpHeaderName.CONTENT_LENGTH.getValue() + ": " + 124 + " "
        );

        // when
        String headersForResponse = responseHeaders.getHeadersForResponse();

        // then
        assertThat(headersForResponse).isEqualTo(expected);
    }
}
