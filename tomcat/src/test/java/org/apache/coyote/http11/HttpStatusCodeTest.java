package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.component.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HttpStatusCodeTest {

    @ParameterizedTest
    @DisplayName("http 응답 문자 확인")
    @EnumSource(HttpStatusCode.class)
    void toHttpResponseText(final HttpStatusCode code) {
        assertThat(code.toHttpResponse()).isEqualTo(code.getCode() + " " + code.getMessage());
    }

}
