package nextstep.org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpStatusCode.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponse.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("HttpResponse를 생성하고 byte 값을 가져온다.")
    void getBytes() {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders()
                .addHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
        final String responseBody = "responseBody";
        final HttpResponse httpResponse = new Builder()
                .statusCode(OK)
                .headers(httpHeaders)
                .body(responseBody)
                .build();

        // when
        final byte[] responseBytes = httpResponse.getBytes();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                responseBody;

        assertThat(responseBytes).isEqualTo(expected.getBytes());
    }
}
