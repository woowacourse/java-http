package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("Http response 파싱 성공")
    void httpRequestHeaders() {
        // given
        var statusCode = HttpStatusCode.OK;
        var headers = String.join("\r\n",
                "Host: localhost:8080 ",
                "Content-Length: 12 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Location: /index.html ",
                "");
        var body = "Hello world!";

        // when
        var sut = new HttpResponse();
        sut.setStatusLine(new HttpStatusLine(HttpStatusCode.OK));
        sut.setHeaders(new HttpHeaders("\r\n" + headers));
        sut.setBody(new HttpBody(body));

        // then
        assertThat(sut.getStatusCode()).isEqualTo(statusCode);
        assertThat(sut.getHeaders().asString()).isEqualTo(headers);
        assertThat(sut.getBody().asString()).isEqualTo(body);
    }
}
