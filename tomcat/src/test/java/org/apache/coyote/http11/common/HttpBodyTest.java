package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpBodyTest {

    @ParameterizedTest(name = "null이 아닌 문자열로 HttpBody를 만들 수 있다.")
    @ValueSource(strings = {"", "testtest", "asdfasdfasdfasdfasdfasdf"})
    void createHttpBodySuccessTest(String text) {
        Assertions.assertDoesNotThrow(() -> HttpBody.from(text));
    }

    @ParameterizedTest(name = "null인 경우, HttpBody를 만들 수 없다.")
    @NullSource
    void createHttpBodyFailTest(String text) {
        assertThatThrownBy(() -> HttpBody.from(text)).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("데이터가 비어있는 HttpBody를 생성할 수 있다.")
    void createEmptyHttpBodyTest() {
        HttpBody emptyHttpBody = HttpBody.createEmptyHttpBody();

        assertThat(emptyHttpBody.getBody()).isEmpty();
    }

    @Test
    @DisplayName("HttpBody의 내용을 삭제할 수 있다,")
    void clearHttpBodyTest() {
        //given
        HttpBody httpBody = HttpBody.from("테스트를 위한 HttpBody 문자열");
        assertThat(httpBody.getBody()).isEqualTo("테스트를 위한 HttpBody 문자열");

        //when
        httpBody.clear();

        //then
        assertThat(httpBody.getBody()).isEmpty();
    }

    @Test
    @DisplayName("HttpBody의 내용에 따라 bytes 길이가 달라진다.")
    void changeHttpBodyBytesLengthTest() {
        //given
        HttpBody httpBody = HttpBody.from("테스트를 위한 HttpBody 문자열");
        assertThat(httpBody.getBytesLength()).isEqualTo("테스트를 위한 HttpBody 문자열".getBytes().length);

        //when
        httpBody.clear();

        //then
        assertThat(httpBody.getBytesLength()).isZero();
    }

}