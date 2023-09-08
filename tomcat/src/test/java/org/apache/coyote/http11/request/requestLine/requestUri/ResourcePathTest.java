package org.apache.coyote.http11.request.requestLine.requestUri;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourcePathTest {

    @Test
    @DisplayName("문자열로 주어진 리소스 경로를 ResourcePath로 변환한다.")
    void stringToResourcePath() {
        // given
        final String resourcePath = "/members";

        // when
        final ResourcePath actual = ResourcePath.from(resourcePath);

        // then
        assertThat(actual.getResourcePath()).isEqualTo(resourcePath);
    }
}
