package org.apache.coyote.http11.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.apache.coyote.http11.common.HttpHeaderType.ACCEPT;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class ResourceContentTypeResolverTest {

    @DisplayName("요청한 HttpHeader와 resource에 맞는 contentType을 반환한다.")
    @ParameterizedTest(name = "contentType: ''{0}'', accept: ''{1}'', resourceName: ''{2}'' -> {3}")
    @CsvSource({
            "text/html, null, null, TEXT_HTML",
            "null, text/javascript, null, TEXT_JAVASCRIPT",
            "null, null, styles.css, TEXT_CSS",
    })
    void getContentType(String contentType, String accept, String resourceName, MediaType expected) {
        // given
        HttpHeaders headers = HttpHeaders.of(Map.of(
                CONTENT_TYPE.getName(), contentType,
                ACCEPT.getName(), accept)
        );

        // when
        ResourceContentTypeResolver resourceContentTypeResolver = new ResourceContentTypeResolver();
        String actual = resourceContentTypeResolver.getContentType(headers, resourceName);

        // then
        assertThat(actual).isEqualTo(expected.stringifyWithUtf());
    }
}
