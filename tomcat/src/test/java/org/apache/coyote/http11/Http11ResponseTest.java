package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11ResponseTest {

    @DisplayName("setHeader와 addHeader를 호출할 수 있다.")
    @Test
    void setHeader_and_addHeader() {
        final var response = new Http11Response();
        final var name = "Set-Cookie";
        final var value1 = "cookie1=value1";
        final var value2 = "cookie2=value2";

        assertThatCode(() -> {
            response.setHeader(name, value1);
            response.addHeader(name, value2);
        }).doesNotThrowAnyException();
    }
}
