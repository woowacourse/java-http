package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.PathParser;
import org.junit.jupiter.api.Test;

class PathParserTest {

    @Test
    void 요청URI의_리소스경로를_얻는다() {
        // given
        final var requestURI = "/index.html";
        final var actual = "jwp-dashboard-http/tomcat/build/resources/main/static/index.html";

        // when
        final var expected = PathParser.parsePath(requestURI).toString();

        // then
        assertThat(expected).contains(actual);
    }
}
