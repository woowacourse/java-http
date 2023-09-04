package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.HttpVersion.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpVersionTest {

    @Test
    void HttpVersion_문자열에_해당하는_HttpVersion이_없는_경우_예외를_던진다() {
        // given
        final String version = "HTTP/5.5";

        // expect
        assertThatThrownBy(() -> HttpVersion.from(version))
                .isInstanceOf(Http11Exception.class)
                .hasMessage("해당 Http Version을 지원할 수 없습니다.");
    }

    @Test
    void HttpVersion_문자열을_입력받아_HttpVersion을_반환한다() {
        // given
        final String version = "HTTP/1.1";

        // when
        final HttpVersion httpVersion = from(version);

        // then
        assertThat(httpVersion).isEqualTo(HttpVersion.HTTP_1_1);
    }
}
