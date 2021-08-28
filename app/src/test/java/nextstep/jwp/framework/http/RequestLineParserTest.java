package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.parser.HeaderLineParser;
import nextstep.jwp.framework.http.parser.LineParser;
import nextstep.jwp.framework.http.parser.RequestLineParser;
import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class RequestLineParserTest {

    @Test
    @DisplayName("상태 줄 파싱 테스트")
    void requestLineParsingTest() {

        // expected
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
        HttpRequest expected = new HttpRequest.Builder().requestLine(requestLine)
                                                        .build();

        // given
        LineParser lineParser = new RequestLineParser();

        // when
        final LineParser nextLineParser = lineParser.parseLine("GET / HTTP/1.1");

        //then
        final HttpRequest httpRequest = nextLineParser.buildRequest();
        assertThat(nextLineParser).isExactlyInstanceOf(HeaderLineParser.class);
        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("토큰이 4개 이상 시 예외 발생 테스트")
    @Test
    void manyTokenParsingTest() {

        // given
        LineParser lineParser = new RequestLineParser();

        // when
        ThrowableAssert.ThrowingCallable callable = () -> lineParser.parseLine("GET / HTTP/1.1 테스트");

        //then
        assertThatIllegalArgumentException().isThrownBy(callable);
    }
}
