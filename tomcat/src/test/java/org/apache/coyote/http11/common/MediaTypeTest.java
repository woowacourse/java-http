package org.apache.coyote.http11.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MediaTypeTest {

    @DisplayName("String 타입의 mediaType이 들어오면, 해당 mediaType에 맞는 MediaType 객체를 반환한다.")
    @ParameterizedTest(name = "''{0}'' -> {1}")
    @CsvSource({
            "text/html, TEXT_HTML",
            "text/javascript, TEXT_JAVASCRIPT",
            "text/css, TEXT_CSS",
            "image/jpeg, IMAGE_JPEG"
    })
    void getMediaType(String mediaType, MediaType expected) {
        // given

        // when
        MediaType actual = MediaType.getMediaType(mediaType);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지원하지 않는 String 타입의 mediaType이 들어오면, null을 반환한다.")
    @Test
    void getMediaType_fail() {
        // given
        String invalidMediaType = "not/supported";

        // when
        MediaType actual = MediaType.getMediaType(invalidMediaType);

        // then
        assertThat(actual).isNull();
    }

    @DisplayName("String 타입의 fileExtenstion이 들어오면, 해당 extension에 맞는 MediaType 객체를 반환한다.")
    @ParameterizedTest(name = "''{0}'' -> {1}")
    @CsvSource({
            "html, TEXT_HTML",
            "js, TEXT_JAVASCRIPT",
            "css, TEXT_CSS",
            "jpeg, IMAGE_JPEG"
    })
    void getMediaTypeByFileExtension(String fileExtension, MediaType expected) {
        // given

        // when
        MediaType actual = MediaType.getMediaTypeByFileExtension(fileExtension);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("지원하지 않는 String 타입의 fileExtenstion이 들어오면, null을 반환한다.")
    @Test
    void getMediaTypeByFileExtension_fail() {
        // given
        String invalidFileExtension = "invalid";

        // when
        MediaType actual = MediaType.getMediaTypeByFileExtension(invalidFileExtension);

        // then
        assertThat(actual).isNull();
    }
}
