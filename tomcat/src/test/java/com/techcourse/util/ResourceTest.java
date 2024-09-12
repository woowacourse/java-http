package com.techcourse.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.techcourse.exception.InvalidResourceException;

class ResourceTest {
    @DisplayName("uri에서 파일명을 추출한다.")
    @ParameterizedTest
    @CsvSource({"/, hello.html", "/index.html, index.html", "/css/styles.css, css/styles.css"})
    void getFileName(String uri, String expectedFileName) {
        // when
        String fileName = Resource.getFileName(uri);

        // then
        assertThat(fileName).isEqualTo(expectedFileName);
    }

    @DisplayName("주어진 파일명을 가진 리소스를 읽는다.")
    @Test
    void read() throws IOException {
        // given
        String fileName = "hello.html";
        String expectedBody = "Hello world!";

        // when
        String result = Resource.read(fileName);

        // then
        assertThat(result).contains(expectedBody);
    }

    @DisplayName("주어진 파일명을 가진 리소스를 찾을 수 없으면 예외가 발생한다.")
    @Test
    void cannotRead() {
        // given
        String fileName = "unknown.html";

        // when & then
        assertThatThrownBy(() -> Resource.read(fileName))
                .isInstanceOf(InvalidResourceException.class);
    }
}
