package org.apache.coyote.http11.domain.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeResolverTest {

    @Test
    @DisplayName("Text 파일의 Content-Type 을 반환한다.")
    void getContentTypeText() throws IOException {
        String contentType = ContentTypeResolver.getContentType("index.html");

        assertThat(contentType).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    @DisplayName("Text 파일이 아닌 Content-Type 을 반환한다.")
    void getContentTypeNotText() throws IOException {
        String contentType = ContentTypeResolver.getContentType("image.png");

        assertThat(contentType).isEqualTo("image/png");
    }
}
