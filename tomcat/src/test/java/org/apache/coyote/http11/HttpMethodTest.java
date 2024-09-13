package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("이름과 일치하는 HttpMethod를 반환한다")
    @Test
    void getHttpMethod() {
        HttpMethod method = HttpMethod.getHttpMethod("GET");

        HttpMethod expected = HttpMethod.GET;

        assertThat(method).isEqualTo(expected);
    }

    @DisplayName("해당하는 HttpMethod가 없을 경우 예외를 발생시킨다")
    @Test
    void notExistHttpMethod() {
        assertThatThrownBy(() -> HttpMethod.getHttpMethod("G"))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("해당 HttpMethod와 일치 여부를 반환한다.")
    @Test
    void isMethod() {
        HttpMethod method = HttpMethod.GET;

        assertAll(
                () -> assertThat(method.isMethod(HttpMethod.GET)).isTrue(),
                () -> assertThat(method.isMethod(HttpMethod.POST)).isFalse()
        );
    }
}
