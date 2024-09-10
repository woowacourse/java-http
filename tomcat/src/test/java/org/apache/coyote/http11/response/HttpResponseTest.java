package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.ResponseHeaders;
import org.apache.coyote.http11.response.startLine.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("응답 메시지를 작성한다.")
    @Test
    void writeTest() {
        String content = "TOMCAT JAZZ";
        ResponseHeaders responseHeaders = ResponseHeaders.of(ContentType.HTML, content);
        ResponseBody responseBody = new ResponseBody(content);
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, responseHeaders, responseBody);

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 11",
                "",
                "TOMCAT JAZZ"
        );

        assertThat(httpResponse.write()).isEqualTo(expected);
    }

}
