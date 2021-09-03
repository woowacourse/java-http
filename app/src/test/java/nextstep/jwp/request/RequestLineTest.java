package nextstep.jwp.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RequestLineTest {

    private final String httpRequest = "GET /index.html HTTP/1.1 ";
    private final String httpRequestWithQueryString = "GET /index.html?account=gugu&password=1234 HTTP/1.1 ";

    @Test
    void of() {
        // when
        RequestLine requestLineWithoutQueryString = RequestLine.of(httpRequest);
        RequestLine expected = new RequestLine(HttpMethod.GET, "/index.html", QueryStringParameters.getEmptyParameters(), "HTTP/1.1");

        // then
        isSameRequestLineWithoutQueryString(requestLineWithoutQueryString, expected);

        // when
        RequestLine requestLineWithQueryString = RequestLine.of(httpRequestWithQueryString);
        RequestLine expected2 = new RequestLine(HttpMethod.GET, "/index.html", QueryStringParameters.of("account=gugu&password=1234"), "HTTP/1.1");

        isSameRequestLineWithQueryString(requestLineWithQueryString, expected2);
    }

    private void isSameRequestLineWithQueryString(RequestLine requestLineWithoutQueryString, RequestLine expected) {
        assertThat(requestLineWithoutQueryString.getHttpMethod()).isEqualTo(expected.getHttpMethod());
        assertThat(requestLineWithoutQueryString.getRequestPath()).isEqualTo(expected.getRequestPath());
        assertThat(requestLineWithoutQueryString.getParameters().existParameter()).isTrue();
        assertThat(requestLineWithoutQueryString.getVersionOfProtocol()).isEqualTo(expected.getVersionOfProtocol());
    }

    private void isSameRequestLineWithoutQueryString(RequestLine requestLineWithoutQueryString, RequestLine expected) {
        assertThat(requestLineWithoutQueryString.getHttpMethod()).isEqualTo(expected.getHttpMethod());
        assertThat(requestLineWithoutQueryString.getRequestPath()).isEqualTo(expected.getRequestPath());
        assertThat(requestLineWithoutQueryString.getParameters().existParameter()).isFalse();
        assertThat(requestLineWithoutQueryString.getVersionOfProtocol()).isEqualTo(expected.getVersionOfProtocol());
    }
}