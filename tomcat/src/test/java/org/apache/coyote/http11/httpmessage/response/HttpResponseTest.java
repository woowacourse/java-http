package org.apache.coyote.http11.httpmessage.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("response객체를 http message 형태로 변경한다.")
    void make_response_form() {
        // given
        final StatusCode ok = StatusCode.OK;
        final String body = "hello world";

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", "11");

        final HttpResponse response = new HttpResponse(ok, new HttpHeader(headers), body);

        // when
        final String result = response.makeToString();

        // then
        final String expect = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 11 \r\n" +
            "\r\n" +
            "hello world";

        assertThat(result).isEqualTo(expect);
    }

}
