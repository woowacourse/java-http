package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestBodyTest {

    @DisplayName("from data에 맞지않는 형식이 들어오면 예외가 발생")
    @ParameterizedTest
    @ValueSource(strings = {"\"account\": \"gugu\"","account=gugu=dodo"})
    void throw_exception_When_input_form_not_match_form_data(String requestBody) {
        assertThatThrownBy(
                () -> HttpRequestBody.toHttpRequestBody("account: gugu", ContentType.APPLICATION_X_WWW_FORM_URLENCODED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 FormUrlEncoded 양식입니다.");
    }

}
