package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    @DisplayName("파일 경로나 확장자로 ContentType을 찾는다.")
    void fromFilePath() {
        final ContentType contentType = ContentType.fromFilePath(".jpeg");
        assertThat(contentType).isSameAs(ContentType.IMAGE_JPEG);
    }
}