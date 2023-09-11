package org.apache.coyote.http11.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceContentTypeResolverTest {

    @DisplayName("요청한 HttpHeader와 resource에 맞는 contentType을 반환한다.")
    @ParameterizedTest(name = "accept: ''{1}'', resourceName: ''{2}'' -> {3}")
    @CsvSource({
            "null, null, ALL",
            "text/javascript, null, TEXT_JAVASCRIPT",
            "null, styles.css, TEXT_CSS",
    })
    void getContentType(String accept, String resourceName, MediaType expected) {
        // given

        // when
        String actual = ResourceContentTypeResolver.getResourceContentType(accept, resourceName);

        // then
        assertThat(actual).isEqualTo(expected.stringifyWithUtf());
    }
}
