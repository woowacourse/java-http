package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ContentTypeTest {

    @Test
    @DisplayName("콘텐츠 타입 응답 스트링 확인")
    void responseText() {
        final ContentType contentType = ContentType.TEXT_HTML;

        assertThat(contentType.toResponseText()).isEqualTo("text/html; charset=utf-8");
    }

    @ParameterizedTest
    @DisplayName("콘텐츠 타입 변환 확인")
    @MethodSource("fromParameter")
    void from(final Path path, final ContentType expected) {
        final ContentType find = ContentType.from(path);

        assertThat(find).isEqualTo(expected);
    }

    static Stream<Arguments> fromParameter() {
        return Stream.of(
                Arguments.of(new Path("/index.html"), ContentType.TEXT_HTML),
                Arguments.of(new Path("/login"), ContentType.TEXT_HTML),
                Arguments.of(new Path("/favicon.ico"), ContentType.IMAGE_ICON)
        );
    }
}
