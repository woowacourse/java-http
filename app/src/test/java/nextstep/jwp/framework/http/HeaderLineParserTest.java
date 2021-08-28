package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.parser.BodyLineParser;
import nextstep.jwp.framework.http.parser.EndLineParser;
import nextstep.jwp.framework.http.parser.HeaderLineParser;
import nextstep.jwp.framework.http.parser.LineParser;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderLineParserTest {

    @Test
    @DisplayName("헤더 줄 파싱 테스트")
    void headerLineParsingTest() {

        // given
        final LineParser lineParser = new HeaderLineParser(createBuilderWithStatusLine());

        // when
        final LineParser nextLineParser = lineParser.parseLine("Content-Type: text/html;charset=utf-8");

        //then
        final HttpRequest actual = createBuilderWithStatusLine().header("Content-Type", "text/html;charset=utf-8")
                                                                .build();

        final HttpRequest httpRequest = nextLineParser.buildRequest();
        assertThat(nextLineParser).isExactlyInstanceOf(HeaderLineParser.class);
        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    @DisplayName("입력 라인이 null 일 경우 EndLine 객체 반환")
    void returnEndLineTest() {

        // given
        final LineParser lineParser = new HeaderLineParser(createBuilderWithStatusLine());

        // when
        final LineParser nextLineParser = lineParser.parseLine(null);

        //then
        assertThat(nextLineParser).isExactlyInstanceOf(EndLineParser.class);
    }

    @Test
    @DisplayName("입력 라인이 공백 일 경우 BodyLine 객체 반환")
    void returnBodyLineTest() {

        // given
        final LineParser lineParser = new HeaderLineParser();

        // when
        final LineParser nextLineParser = lineParser.parseLine(" ");

        //then
        assertThat(nextLineParser).isExactlyInstanceOf(BodyLineParser.class);
    }

    private static HttpRequest.Builder createBuilderWithStatusLine() {
        return new HttpRequest.Builder().requestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
    }
}
