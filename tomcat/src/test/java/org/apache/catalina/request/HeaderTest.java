package org.apache.catalina.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderTest {

    @DisplayName("헤더에 일치하는 값을 반환한다.")
    @Test
    void of() {
        Header header = Header.of("Cookie");

        assertThat(header).isEqualTo(Header.COOKIE);
    }
}
