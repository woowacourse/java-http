package org.apache.coyote.http;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class ContentTypeTest {

    @Test
    @DisplayName("ContentType을 생성한다.")
    void of() {
        assertThat(ContentType.from("css")).isEqualTo(ContentType.CSS);
    }
}
