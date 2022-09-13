package org.apache.coyote.http11.constant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpMethodsTest {

    @DisplayName("Http 메소드명으로 HttpMethods Enum을 가져온다.")
    @ParameterizedTest
    @ValueSource(strings = {"get", "post"})
    void toHttpMethod(String method) {
        HttpMethod httpMethods = HttpMethod.toHttpMethod(method);

        assertThat(httpMethods.getName()).isEqualTo(method);
    }
}