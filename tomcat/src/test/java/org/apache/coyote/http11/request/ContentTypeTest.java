package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void 올바른_컨텐츠를_찾는지_확인한다() {
        String filename = "wooteco.js";

        ContentType content = ContentType.findByUrl(filename);

        assertThat(content).isEqualTo(ContentType.JAVASCRIPT);
    }
}
