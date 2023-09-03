package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ResourceContentType 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResourceContentTypeTest {

    @Test
    void 요청_URI로부터_컨텐츠타입_추출() {
        // given
        final String uri = "/index.html";

        // when
        final ResourceContentType resourceContentType = ResourceContentType.from(uri);

        // then
        assertThat(resourceContentType).isEqualTo(ResourceContentType.HTML);
    }

    @Test
    void 요청_URI로부터_컨텐츠타입_추출2() {
        // given
        final String uri = "/styles.css";

        // when
        final ResourceContentType resourceContentType = ResourceContentType.from(uri);

        // then
        assertThat(resourceContentType).isEqualTo(ResourceContentType.CSS);
    }
}
