package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HeaderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseHeadersTest {

    @Test
    @DisplayName("Header에 값을 저장할 수 있다.")
    void setHeaders() {
        //given
        HttpResponseHeaders responseHeaders = HttpResponseHeaders.createEmptyHeaders();

        //when
        responseHeaders.setContentType(ContentType.TEXT_CSS.getValue());

        //then
        assertThat(responseHeaders.get(HeaderType.CONTENT_TYPE.getValue()))
                .isEqualTo(ContentType.TEXT_CSS.getValue());
    }

    @Test
    @DisplayName("Header에 저장되어 있는 값을 응답 문자열 형식으로 반환할 수 있다.")
    void getHeadersForResponse() {
        //given
        HttpResponseHeaders responseHeaders = HttpResponseHeaders.createEmptyHeaders();

        //when
        responseHeaders.setContentType(ContentType.TEXT_CSS.getValue());
        responseHeaders.setContentLength("123456");

        String expected = String.join(System.lineSeparator(),
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: 123456 ");

        //then
        assertThat(responseHeaders.getHeaders()).isEqualTo(expected);
    }


}