package org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotFoundResourceException;
import org.junit.jupiter.api.Test;

class PathParserTest {

    @Test
    void 요청URI의_리소스경로를_얻는다() {
        // given
        final var requestURI = "/index.html";
        final var expected = "jwp-dashboard-http/tomcat/build/resources/main/static/index.html";

        // when
        final var actual = PathParser.parsePath(requestURI).toString();

        // then
        assertThat(actual).contains(expected);
    }

    @Test
    void 요청URI의_리소스가_존재하지_않는_경우_예외가_발생한다() {
        // given
        final var requestURI = "/notExist.html";

        // when, then
        assertThatThrownBy(() -> PathParser.parsePath(requestURI))
                .isInstanceOf(NotFoundResourceException.class);
    }
}
