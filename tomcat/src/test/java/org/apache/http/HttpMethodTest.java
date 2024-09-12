package org.apache.http;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    @DisplayName("동일한 HttpMethod 인스턴스 여부 확인.")
    void isSameMethod() {
        assertAll(
                () -> assertTrue(HttpMethod.POST.isSameMethod(HttpMethod.POST)),
                () -> assertTrue(HttpMethod.PUT.isSameMethod(HttpMethod.PUT)),
                () -> assertTrue(HttpMethod.PATCH.isSameMethod(HttpMethod.PATCH))
        );
    }
}
