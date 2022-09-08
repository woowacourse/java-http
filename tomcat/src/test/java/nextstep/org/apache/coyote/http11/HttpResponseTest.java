package nextstep.org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpHeader.LOCATION;
import static org.apache.coyote.http11.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.HttpStatusCode.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("HttpResponse를 생성하고 byte 값을 가져온다.")
    void getBytes() {
        // given
        final String responseBody = "responseBody";
        final HttpResponse httpResponse = new HttpResponse()
                .statusCode(OK)
                .addHeader(CONTENT_TYPE, "text/html;charset=utf-8")
                .responseBody(responseBody);

        // when
        final byte[] responseBytes = httpResponse.getBytes();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                responseBody;

        assertThat(responseBytes).isEqualTo(expected.getBytes());
    }

    @Test
    @DisplayName("HttpResponse를 생성하고 byte 값을 가져온다.(response body가 없는 경우)")
    void getBytesWithoutResponseBody() {
        // given
        final HttpResponse httpResponse = new HttpResponse()
                .statusCode(FOUND)
                .addHeader(LOCATION, "/index.html");

        // when
        final byte[] responseBytes = httpResponse.getBytes();

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n";

        assertThat(responseBytes).isEqualTo(expected.getBytes());
    }
}
