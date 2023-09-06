package org.apache.coyote.http.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.Response;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import servlet.response.HttpResponse;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseGeneratorTest {

    @Test
    void generate_메서드는_HttpRequest와_추가_헤더가_없는_HttpResponse를_전달하면_Response로_변환해_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());
        final HttpResponse httpResponse = new HttpResponse();

        final Response actual = ResponseGenerator.generate(httpRequest, httpResponse);

        assertThat(actual).isNotNull();
    }

    @Test
    void generate_메서드는_HttpRequest와_추가_헤더가_있는_HttpResponse를_전달하면_Response로_변환해_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());
        final HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHeader("X-Auth", "accessToken");

        final Response actual = ResponseGenerator.generate(httpRequest, httpResponse);

        assertThat(actual).isNotNull();
    }
}
