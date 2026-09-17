package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceTest {

    @Test
    void createStaticResource() {
        // given
        final String body = "Hello world!";
        final String contentType = "text/html";

        // when
        final StaticResource staticResource = new StaticResource(body, contentType);

        // then
        assertThat(staticResource.getBody()).isEqualTo(body);
        assertThat(staticResource.getContentType()).isEqualTo(contentType);
    }
}
