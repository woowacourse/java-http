package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResourceUtilTest {

    @Test
    void ContentType을_찾는다() {
        final String actual = ResourceUtil.getContentType("/index.html");
        assertThat(actual).isEqualTo("text/html");
    }
}
