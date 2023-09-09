package org.apache.coyote.http11.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceReaderTest {

    @DisplayName("resource 요청이 들어왔을 때, 해당 리소스의 위치를 URL 객체로 반환한다.")
    @Test
    void getResourceUrl() {
        // given
        String resourceName = "/index.html";

        // when
        URL actual = ResourceReader.getResourceUrl(resourceName);

        // then
        assertThat(actual.getFile()).endsWith(resourceName);
    }

    @DisplayName("존재하지 않는 resource 요청이 들어왔을 때, null 을 반환한다.")
    @Test
    void getResourceUrl_fail() {
        // given
        String resourceName = "/invalid.html";

        // when
        URL actual = ResourceReader.getResourceUrl(resourceName);

        // then
        assertThat(actual).isNull();
    }
}
