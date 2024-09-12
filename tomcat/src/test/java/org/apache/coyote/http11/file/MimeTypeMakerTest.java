package org.apache.coyote.http11.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class MimeTypeMakerTest {

    static Stream<Arguments> testDataForGetMimeTypeFromExtension() {
        return Stream.of(
                Arguments.of("html", "text/html"),
                Arguments.of("PNG", "image/png"),
                Arguments.of("webp", "application/octet-stream")
        );
    }

    @DisplayName("extension으로 MIME Type을 만들 수 있다.")
    @MethodSource("testDataForGetMimeTypeFromExtension")
    @ParameterizedTest
    void testGetMimeTypeFromExtension(final String extension, final String expected) {
        // given & when
        final String actual = MimeTypeMaker.getMimeTypeFromExtension(extension);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
