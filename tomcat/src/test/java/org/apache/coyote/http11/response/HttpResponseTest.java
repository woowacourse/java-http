package org.apache.coyote.http11.response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void httpResponse_생성_테스트() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHttpResponseStartLine(StatusCode.OK);
        httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
        byte[] body = "Hello World".getBytes();
        httpResponse.setResponseBody(body);
        httpResponse.addHeader("Content-Length", String.valueOf(body.length));

        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "\r\n" +
                "Hello World";
        Assertions.assertThat(httpResponse.generateResponse()).isEqualTo(expected.getBytes());
    }
}
