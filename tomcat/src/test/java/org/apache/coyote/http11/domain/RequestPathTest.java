package org.apache.coyote.http11.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestPathTest {

    @Test
    @DisplayName("경로가 Null인 경우 에러를 발생한다.")
    void getPath() {
        assertThrows(NullPointerException.class, () -> new RequestPath(null));
    }
}
