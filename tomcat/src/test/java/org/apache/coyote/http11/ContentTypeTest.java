package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.apache.coyote.component.ContentType;
import org.apache.coyote.component.Path;
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
                Arguments.of(Path.from("/index.html"), ContentType.TEXT_HTML),
                Arguments.of(Path.from("/login"), ContentType.TEXT_HTML),
                Arguments.of(Path.from("/favicon.ico"), ContentType.IMAGE_ICON)
        );
    }
}
