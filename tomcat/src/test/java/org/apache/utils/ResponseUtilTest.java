package org.apache.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResponseUtilTest {

    @Test
    @DisplayName("파일이름으로 파일내용을 읽는다.")
    void checkFileByFilename() {
        // given
        String filename = "/index.html";

        // when
        String actual = ResponseUtil.getResponseBody(filename, this.getClass());

        // then
        assertThat(actual).isNotBlank();
    }

    @Test
    @DisplayName("파일이름이 없는 경우 예외를 발생한다..")
    void checkNonFile() {
        // given
        String filename = "/nonFile";

        // when & then
        assertThatThrownBy(() -> ResponseUtil.getResponseBody(filename, this.getClass()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}