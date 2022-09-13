package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HtmlResponseTest {

    @Test
    @DisplayName("html파일 Response객체를 생성한다.")
    void of() {
        final HttpResponse httpResponse = HtmlResponse.of(HttpStatus.OK, HttpHeaders.empty(), "test");

        final String httpMessage = httpResponse.toHttpMessage();
        final String expected = "HTTP/1.1 200 OK \r\n"
                + "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: 8 \r\n"
                + "\r\n"
                + "testHtml";
        assertThat(httpMessage).isEqualTo(expected);
    }
}