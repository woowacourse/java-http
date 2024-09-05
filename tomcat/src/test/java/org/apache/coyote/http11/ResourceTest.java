package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceTest {
    @Test
    @DisplayName("파일명에서 .을 기반으로 확장자를 반환한다.")
    void extension_separate_comma() {
        final Resource resource = new Resource("main.html", new byte[]{});
        final FileExtension ext = resource.getExtension();
        assertThat(ext).isEqualTo(FileExtension.HTML);
    }
}
