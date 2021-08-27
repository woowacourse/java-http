package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestLineParserTest {

    @Test
    @DisplayName("상태 줄 파싱 테스트")
    public void statusLineParsingTest() {

        // given
        LineParser lineParser = new RequestLineParser();
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", "HTTP/1.1");

        // when
        final LineParser nextLineParser = lineParser.parseLine("GET / HTTP/1.1");

        //then
        HttpRequest actual = new HttpRequestBuilder().requestLine(requestLine)
                                                     .build();

        final HttpRequest httpRequest = nextLineParser.buildRequest();
        assertThat(nextLineParser).isExactlyInstanceOf(HeaderLineParser.class);
        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(actual);
    }
}
