package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusLineTest {

    @Test
    @DisplayName("상태 줄 파싱 테스트")
    public void statusLineParsingTest() {

        // given
        ParsingLine parsingLine = new StatusLine();

        // when
        final ParsingLine nextLine = parsingLine.parse("GET / HTTP/1.1");

        //then
        HttpRequest actual = new HttpRequestBuilder().httpMethod(HttpMethod.GET)
                                                     .uri("/")
                                                     .version("HTTP/1.1")
                                                     .build();

        final HttpRequest httpRequest = nextLine.buildRequest();
        assertThat(nextLine).isExactlyInstanceOf(HeaderLine.class);
        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(actual);
    }
}
