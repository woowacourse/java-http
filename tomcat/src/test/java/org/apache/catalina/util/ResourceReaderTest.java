package org.apache.catalina.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.apache.catalina.exception.CatalinaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceReaderTest {

    @DisplayName("Content-Type을 반환한다.")
    @Test
    void probeContentType() {
        // given
        String path = "/css/styles.css";

        // when&then
        assertThat(ResourceReader.probeContentType(path)).isEqualTo("text/css");
    }

    @DisplayName("요청 경로의 파일을 읽어온다.")
    @Test
    void read() {
        // given
        String path = "/index";

        // when&then
        assertThat(ResourceReader.read(path)).contains("<title>대시보드</title>");
    }

    @DisplayName("요청 경로의 파일이 존재하지 않으면 예외를 던진다.")
    @Test
    void read_fileNotFound() {
        // given
        String path = "/notFile";

        // when&then
        assertThatThrownBy(() -> ResourceReader.read(path)).isInstanceOf(CatalinaException.class);
    }

    @DisplayName("요청 경로의 파일이 존재하지 않으면 null을 반환한다.")
    @Test
    void getResource_fileNotFound() {
        // given
        String path = "/notFile";

        // when&then
        assertThat(ResourceReader.getResource(path)).isNull();
    }
}