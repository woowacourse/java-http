package nextstep.jwp.framework.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderParserTest {

    @Test
    @DisplayName("헤더 1개 파싱 테스트")
    void oneHeaderParsingTest() throws IOException {

        // given
        String given = "Host: localhost:8080\r\n\r\n";
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
        final HttpRequest.Builder builder = new HttpRequest.Builder().requestLine(requestLine);
        HttpParser httpParser = new HeaderParser(new BufferedReader(new StringReader(given)), builder);

        // when
        final HttpRequest httpRequest = httpParser.parse().buildRequest();

        //then
        HttpRequest expected = new HttpRequest.Builder().requestLine(requestLine)
                                                        .header("Host", "localhost:8080")
                                                        .build();

        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("헤더 여러 개 파싱 테스트")
    void manyHeaderParsingTest() throws IOException {

        // given
        String given = "Host: localhost:8080\r\nConnection: keep-alive\r\n\r\n";
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
        final HttpRequest.Builder builder = new HttpRequest.Builder().requestLine(requestLine);
        HttpParser httpParser = new HeaderParser(new BufferedReader(new StringReader(given)), builder);

        // when
        final HttpRequest httpRequest = httpParser.parse().buildRequest();

        //then
        HttpRequest expected = new HttpRequest.Builder().requestLine(requestLine)
                                                        .header(HttpHeaders.HOST, "localhost:8080")
                                                        .header(HttpHeaders.CONNECTION, "keep-alive")
                                                        .build();

        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(expected);
    }
}
