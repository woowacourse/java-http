package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.apache.coyote.http11.response.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("ContentType 클래스의")
class ContentTypeTest {

    @Nested
    @DisplayName("findByUri 메서드는")
    @TestInstance(Lifecycle.PER_CLASS)
    class FindByUri {

        @ParameterizedTest(name = "{0} 요청 시 {1} 타입을 반환한다.")
        @MethodSource("contentTypeArguments")
        @DisplayName("주어진 정적 파일에 해당하는 컨텐트 타입을 반환한다.")
        void success(final String fileUri, final ContentType expected) {
            // given & when
            final ContentType actual = ContentType.findByUri(fileUri);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("주어진 정적 파일에 해당하는 컨텐트 타입이 존재하지 않는 경우 예외를 던진다.")
        void invalidContentType_ExceptionThrown() {
            // given
            final String fileUri = "index.home";

            // when & then
            assertThatThrownBy(() -> ContentType.findByUri(fileUri))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("파일 확장자를 찾을 수 없습니다.");
        }

        private Stream<Arguments> contentTypeArguments() {
            return Stream.of(Arguments.of("index.html", ContentType.HTML),
                    Arguments.of("styles.css", ContentType.CSS),
                    Arguments.of("scripts.js", ContentType.JAVASCRIPT),
                    Arguments.of("error-404-monochrome.svg", ContentType.SVG));
        }
    }
}
