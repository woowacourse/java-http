package com.techcourse.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileUtilTest {

    @DisplayName("슬래시로 시작하는 경로인 경우")
    @Test
    void createFileNameTest1() {
        // given
        String path = "/index.html";

        // when
        String fileName = FileUtil.createFileName(path);

        // then
        assertThat(fileName).isEqualTo("/static/index.html");
    }

    @DisplayName("슬래시로 시작하지 않는 경로인 경우")
    @Test
    void createFileNameTest2() {
        // given
        String path = "styles.css";

        // when
        String fileName = FileUtil.createFileName(path);

        // then
        assertThat(fileName).isEqualTo("/static/styles.css");
    }

    @DisplayName("루트 경로인 경우")
    @Test
    void createFileNameTest3() {
        // given
        String path = "/";

        // when
        String fileName = FileUtil.createFileName(path);

        // then
        assertThat(fileName).isEqualTo("/static/");
    }

    @DisplayName("중첩 경로인 경우")
    @Test
    void createFileNameTest4() {
        // given
        String path = "/css/styles.css";

        // when
        String fileName = FileUtil.createFileName(path);

        // then
        assertThat(fileName).isEqualTo("/static/css/styles.css");
    }

    @DisplayName("존재하는 파일인 경우")
    @Test
    void readResourceTest1() {
        // given
        String fileName = "/static/index.html";

        // when
        String content = FileUtil.readResource(fileName);

        // then
        assertThat(content).isNotNull();
        assertThat(content).contains("<!DOCTYPE html>");
    }

    @DisplayName("존재하지 않는 파일인 경우")
    @Test
    void readResourceTest2() {
        // given
        String fileName = "/static/nonexistent.html";

        // when & then
        assertThatThrownBy(() -> FileUtil.readResource(fileName))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 파일입니다.");
    }
}
