package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class BodyLineParserTest {

    @Test
    @DisplayName("바디 줄 파싱 테스트")
    public void bodyLineParsingTest() {

        // given
        final LineParser lineParser = new BodyLineParser();

        // when
        final LineParser nextLineParser = lineParser.parseLine("Hello World!");

        //then
        final HttpRequest actual = new HttpRequest.Builder().body("Hello World!")
                                                            .build();

        final HttpRequest httpRequest = nextLineParser.buildRequest();
        assertThat(nextLineParser).isExactlyInstanceOf(BodyLineParser.class);
        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "", " "})
    @DisplayName("입력 라인이 null 혹은 공백일 경우 EndLine 객체 반환")
    public void returnEndLineTest(String body) {

        // given
        if ("null".equals(body)) {
            body = null;
        }

        final LineParser lineParser = new BodyLineParser();

        // when
        final LineParser nextLineParser = lineParser.parseLine(body);

        //then
        final HttpRequest actual = new HttpRequest.Builder().build();
        final HttpRequest httpRequest = nextLineParser.buildRequest();
        assertThat(nextLineParser).isExactlyInstanceOf(EndLineParser.class);
        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(actual);
    }
}
