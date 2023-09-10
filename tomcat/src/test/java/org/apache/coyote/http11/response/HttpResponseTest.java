package org.apache.coyote.http11.response;

import org.apache.catalina.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void http_response_생성_테스트() {
        Response response = new Response();
        response.setHttpResponseStartLine(StatusCode.OK);
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        byte[] body = "Hello World".getBytes();
        response.setResponseBody(body);

        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHttpResponseStartLine(response.getHttpResponseStartLine());
        httpResponse.setHttpResponseHeaders(response.getHttpResponseHeaders());
        httpResponse.setResponseBody(response.getResponseBody());

        String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "\r\n" +
                "Hello World";

        Assertions.assertThat(httpResponse.generateResponse()).isEqualTo(expected.getBytes());
    }
}
