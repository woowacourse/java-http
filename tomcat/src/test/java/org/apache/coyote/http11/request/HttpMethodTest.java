package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class HttpMethodTest {

    @CsvSource({"POST, POST", "GET, GET", "post, POST", "GeT, GET"})
    @ParameterizedTest
    @DisplayName("문자열로 입력받은 HttpMethod를 정의된 HttpMethod 형식으로 반환한다.")
    void from(String method, String httpMethod) {
        //when
        HttpMethod result = HttpMethod.from(method);

        //then
        assertThat(result).isEqualTo(HttpMethod.valueOf(httpMethod));
    }

    @NullAndEmptySource
    @CsvSource({"POSTT", "DELETE", "GET+POST"})
    @ParameterizedTest
    @DisplayName("정의되지 않은 문자열을 입력받으면 NONE을 반환한다.")
    void from_InvalidInput(String method) {
        //when
        HttpMethod result = HttpMethod.from(method);

        //then
        assertThat(result).isEqualTo(HttpMethod.NONE);
    }

    @EnumSource(value = HttpMethod.class, mode = EnumSource.Mode.EXCLUDE, names = "NONE")
    @ParameterizedTest
    @DisplayName("HttpMethod에 정의된 메서드라면 true를 반환한다.")
    void isValidMethod_true(HttpMethod httpMethod) {
        //when
        boolean isValid = httpMethod.isValidMethod();

        //then
        assertThat(isValid).isTrue();
    }

    @NullAndEmptySource
    @CsvSource({"POSTT", "DELETE"})
    @ParameterizedTest
    @DisplayName("HttpMethod에 정의되지 않은 메서드라면 false를 반환한다.")
    void isValidMethod_false(String method) {
        //when
        HttpMethod result = HttpMethod.from(method);

        //then
        assertThat(result.isValidMethod()).isFalse();
    }
}
