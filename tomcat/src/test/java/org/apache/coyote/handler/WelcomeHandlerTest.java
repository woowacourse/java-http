package org.apache.coyote.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.handler.WelcomeHandler;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WelcomeHandlerTest {

    @Test
    void 생성자는_rootContextPath를_전달하면_WelcomeHandler를_초기화한다() {
        final WelcomeHandler actual = new WelcomeHandler();

        assertThat(actual).isNotNull();
    }

    @Test
    void supports_메서드는_지원하는_요청인_경우_true를_반환한다() {
        final WelcomeHandler handler = new WelcomeHandler();
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET / HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = handler.supports(request,"/");

        assertThat(actual).isTrue();
    }

    @Test
    void supports_메서드는_지원하지_않는_요청인_경우_false를_반환한다() {
        final WelcomeHandler handler = new WelcomeHandler();
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET / HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = handler.supports(request, "/hello");

        assertThat(actual).isFalse();
    }

    @Test
    void service_메서드는_요청을_처리하고_Response를_반환한다() {
        final WelcomeHandler handler = new WelcomeHandler();
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET / HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final Response actual = handler.service(request, "ignored");

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isNotNull();
            softAssertions.assertThat(actual.convertResponseMessage())
                          .contains("HTTP/1.1")
                          .contains("200 OK")
                          .contains("Content-Type: text/html;charset=utf-8")
                          .contains("Content-Length: 12")
                          .contains("Hello World!");
        });
    }
}
