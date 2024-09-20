package org.apache.coyote.http11.response;

import org.apache.coyote.http11.constants.ContentType;
import org.apache.coyote.http11.constants.HttpHeader;
import org.apache.coyote.http11.constants.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("응답을 바이트 배열로 직렬화한다")
    @Test
    void serializeResponse() {

        final HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getContentTypeUtf8());
        httpResponse.setBody("test body");

        String expected = """
                HTTP/1.1 200 OK \r
                Content-Type: text/html; charset=utf-8 \r
                Content-Length: 9 \r
                \r
                test body""";

        Assertions.assertThat(new String(httpResponse.serializeResponse())).isEqualTo(expected);
    }

    @DisplayName("바디를 추가할때 헤더에 Content-Length도 넣어준다")
    @Test
    void insert_content_length_when_exist_body () {
        final HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setBody("test body");

        String expected = """
                HTTP/1.1 200 OK \r
                Content-Length: 9 \r
                \r
                test body""";

        Assertions.assertThat(new String(httpResponse.serializeResponse())).isEqualTo(expected);
    }
}
