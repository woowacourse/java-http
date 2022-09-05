package nextstep.jwp.http;

import org.apache.http.HttpHeader;
import org.apache.http.HttpMime;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static nextstep.fixtures.HttpFixtures.요청을_생성한다;
import static org.apache.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class RequestParserTest {

    @Test
    void HttpMessage의_첫째줄인_요청_정보를_반환한다() {
        // given
        final String rawRequest = 요청을_생성한다(GET, "/index.html", HttpMime.TEXT_HTML);
        final InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(rawRequest.getBytes()));
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final RequestInfo expected = new RequestInfo(GET, "/index.html");

        // when
        final Request actual = RequestParser.parse(bufferedReader);

        // then
        assertThat(actual.getRequestInfo()).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 요청_헤더를_반환한다() {
        // given
        final String rawRequest = 요청을_생성한다(GET, "/index.html", HttpMime.TEXT_HTML);
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
        final String expected = String.join("\r\n", "account=gugu", "password=password");
        final String rawRequest = 요청을_생성한다(GET, "/index.html", HttpMime.TEXT_HTML, expected);
        final InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(rawRequest.getBytes()));
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // when
        final Request actual = RequestParser.parse(bufferedReader);

        // then
        assertThat(actual.getBody()).isEqualTo(expected);
    }
}
