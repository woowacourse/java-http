package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseTest {

    @Test
    void of_메서드는_필요한_데이터를_전달하면_Response를_초기화한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET / HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final String body = "Hello World!";

        final Response actual = Response.of(request, HttpStatusCode.OK, ContentType.TEXT_HTML, body);

        assertThat(actual).isNotNull();
    }

    @Test
    void convertResponseMessage_메서드는_Response를_문자열로_변환해_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET / HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final String body = "Hello World!";
        final Response response = Response.of(request, HttpStatusCode.OK, ContentType.TEXT_HTML, body);

        final String actual = response.convertResponseMessage();

        assertThat(actual).contains("HTTP/1.1 200 OK")
                          .contains("Content-Type: text/html;charset=utf-8")
                          .contains("Content-Length: 12")
                          .contains(body);
    }
}
