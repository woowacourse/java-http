package nextstep.jwp.http;

import org.apache.http.HttpHeader;
import org.apache.http.HttpMethod;
import org.apache.http.HttpMime;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

class RequestParserTest {

    @Test
    void HttpMessage의_첫째줄인_요청_정보를_반환한다() {
        // given
        final String rawRequest = makeGetRequestWithBody("/index.html", HttpMime.TEXT_HTML.getValue());
        final InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(rawRequest.getBytes()));
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final RequestInfo expected = new RequestInfo(HttpMethod.GET, "/index.html");

        // when
        final Request actual = RequestParser.parse(bufferedReader);

        // then
        assertThat(actual.getRequestInfo()).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 요청_헤더를_반환한다() {
        // given
        final String rawRequest = makeGetRequestWithBody("/index.html", HttpMime.TEXT_HTML.getValue());
        final InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(rawRequest.getBytes()));
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final Headers expected = new Headers();
        expected.put(HttpHeader.HOST, "localhost:8080");
        expected.put(HttpHeader.CONNECTION, "keep-alive");
        expected.put(HttpHeader.ACCEPT, "text/html,*/*;q=0.1");

        // when
        final Request actual = RequestParser.parse(bufferedReader);

        // then
        assertThat(actual.getHeaders()).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 요청바디를_반환한다() {
        // given
        final String rawRequest = makeGetRequestWithBody("/index.html", HttpMime.TEXT_HTML.getValue());
        final InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(rawRequest.getBytes()));
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final String expected = String.join("\r\n", "account=gugu", "password=password");

        // when
        final Request actual = RequestParser.parse(bufferedReader);

        // then
        assertThat(actual.getBody()).isEqualTo(expected);
    }

    private String makeGetRequestWithBody(final String uri, final String contentType) {
        return String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: " + contentType + ",*/*;q=0.1",
                "",
                "account=gugu",
                "password=password");
    }
}
