package nextstep.jwp.httpmessage;

import nextstep.jwp.httpmessage.httpresponse.HttpResponse;
import nextstep.jwp.httpmessage.httpresponse.HttpStatusCode;
import nextstep.jwp.httpmessage.httpresponse.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.stream.Stream;

import static nextstep.jwp.httpmessage.httprequest.HttpMessageReader.CRLF;
import static nextstep.jwp.httpmessage.httprequest.HttpMessageReader.SP;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpResponse 테스트")
class HttpResponseTest {

    private static Stream<Arguments> responseHttpMessage() {
        return Stream.of(
                Arguments.of(HttpVersion.HTTP1_1, HttpStatusCode.FOUND, "Location", "/index.html", null),
                Arguments.of(HttpVersion.HTTP1_1, HttpStatusCode.OK, "Content-Type", "text/html;charset=utf-8", "ok"),
                Arguments.of(HttpVersion.HTTP1_1, HttpStatusCode.OK, null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void responseHttpMessage(HttpVersion httpVersion, HttpStatusCode httpStatusCode, String headerKey, String headerValue, Object responseBody) {
        //given
        final HttpResponse httpResponse = new HttpResponse();
        //when
        httpResponse.setStatusLine(new StatusLine(httpVersion, httpStatusCode));
        httpResponse.addHeader(headerKey, headerValue);
        httpResponse.setBody(responseBody);
        final String expectedResponseMessage = toHttpResponseMessage(httpResponse.getStatusLine(), httpResponse.getHttpHeadersAsString(), responseBody);
        //then
        assertThat(httpResponse.getStatusLineAsString()).isEqualTo(httpVersion.getValue() + SP + httpStatusCode.getValue() + SP + httpStatusCode.getReasonPhrase());
        assertThat(httpResponse.getHttpVersion()).isEqualTo(httpVersion);
        assertThat(httpResponse.getHttpStatusCode()).isEqualTo(httpStatusCode);
        assertThat(httpResponse.getHeader(headerKey)).isEqualTo(headerValue);
        assertThat(httpResponse.getHttpHeaders()).isNotNull();
        assertThat(httpResponse.getHttpMessage()).isEqualTo(expectedResponseMessage);
    }

    private String toHttpResponseMessage(StatusLine statusLine, String httpHeaderString, Object body) {
        if (Objects.isNull(body)) {
            body = "";
        }
        return String.join(CRLF,
                statusLine.getLine() + SP,
                httpHeaderString,
                body.toString());
    }
}