package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

    @Test
    @DisplayName("입력 문자열을 통해, Http Version을 생성할 수 있다.")
    void createHttpVersionFromInput() {
        String input = "HTTP/1.1";

        assertThat(HttpVersion.from(input)).isEqualTo(HttpVersion.V1_1);
    }

    @Test
    @DisplayName("잘못된 문자열을 입력하면, Http Version 생성 과정에서 예외가 발생한다.")
    void createHttpVersionFromInvalidInput() {
        String input = "HTTP/9.9";

        assertThatThrownBy(() -> HttpVersion.from(input))
                .isInstanceOf(BadRequestException.class);
    }

}