package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderLineTest {

    @Test
    @DisplayName("헤더 줄 파싱 테스트")
    public void headerLineParsingTest() {

        // given
        final ParsingLine parsingLine = new HeaderLine(createBuilderWithStatusLine());

        // when
        final ParsingLine nextLine = parsingLine.parse("Content-Type: text/html;charset=utf-8\r\n");

        //then
        final HttpRequest actual = createBuilderWithStatusLine().header("Content-Type", "text/html;charset=utf-8")
                                                                .build();

        final HttpRequest httpRequest = nextLine.buildRequest();
        assertThat(nextLine).isExactlyInstanceOf(HeaderLine.class);
        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    @DisplayName("입력 라인이 null 일 경우 EndLine 객체 반환")
    public void returnEndLineTest() {

        // given
        final ParsingLine parsingLine = new HeaderLine(createBuilderWithStatusLine());

        // when
        final ParsingLine nextLine = parsingLine.parse(null);

        //then
        assertThat(nextLine).isExactlyInstanceOf(EndLine.class);
    }

    @Test
    @DisplayName("입력 라인이 공백 일 경우 BodyLine 객체 반환")
    public void returnBodyLineTest() {

        // given
        final ParsingLine parsingLine = new HeaderLine();

        // when
        final ParsingLine nextLine = parsingLine.parse(" ");

        //then
        assertThat(nextLine).isExactlyInstanceOf(BodyLine.class);
    }

    private static HttpRequestBuilder createBuilderWithStatusLine() {
        return new HttpRequestBuilder().httpMethod(HttpMethod.GET)
                                       .uri("/")
                                       .version("HTTP/1.1");
    }
}
