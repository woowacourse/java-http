package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.catalina.exception.FileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileTest {

    @DisplayName("존재하지 않는 파일을 생성하는 경우 예외가 발생한다.")
    @Test
    void fileNotFound() {
        assertThatThrownBy(() -> File.of("/notExist.html"))
                .isInstanceOf(FileException.class)
                .hasMessage("존재하지 않는 파일입니다.");
    }
}
