package org.apache.coyote.common.request.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("RequestLineParser 는 ")
class RequestLineParserTest {

    @DisplayName("요청 url을 통해 정적 파일 경로를 생성한다.")
    @ParameterizedTest(name = "{index} {displayName} requestUrl={0} expected={1}")
    @ValueSource(strings = {"/", "/index", "/index.html"})
    void getStaticResourcePath(final String path) {
        final String expected = "static/index.html";

        assertThat(RequestLineParser.getStaticResourcePath(path)).isEqualTo(expected);
    }
}
