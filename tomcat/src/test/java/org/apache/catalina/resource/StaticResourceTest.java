package org.apache.catalina.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.response.ContentType;
import org.junit.jupiter.api.Test;

class StaticResourceTest {

    @Test
    void 확장자가_없는_경로는_html_파일을_찾는다() throws IOException {
        StaticResource resource = StaticResource.from("/login");

        assertThat(resource.getContentType()).isEqualTo(ContentType.HTML);
        assertThat(resource.getContent()).contains("<title>로그인</title>");
    }

    @Test
    void 확장자에_맞는_컨텐츠_타입을_갖는다() throws IOException {
        StaticResource resource = StaticResource.from("/css/styles.css");

        assertThat(resource.getContentType()).isEqualTo(ContentType.CSS);
    }
}
