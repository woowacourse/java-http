package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HeaderName;
import org.apache.coyote.http.StatusCode;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 상태코드를_추가한다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.setStatusCode(StatusCode.OK);

        // then
        String expected = "HTTP/1.1 200 OK\r\n";
        assertThat(response.getReponse()).startsWith(expected);
    }

    @Test
    void 헤더를_추가한다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.addHeader(HeaderName.CONTENT_TYPE, "text/html");

        // then
        assertThat(response.getReponse()).contains("Content-Type: text/html");
    }

    @Test
    void 바디를_추가한다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.setBody("/index.html");

        // then
        assertThat(response.getReponse()).contains("<title>대시보드</title>");
    }
}
