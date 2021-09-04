package nextstep.jwp.http.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileExtensionTest {

    @DisplayName("FileExtension 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"HTML", "html"})
    void create(String extension) {
        FileExtension fileExtension = FileExtension.from(extension);
        assertThat(fileExtension).isSameAs(FileExtension.HTML);
    }

    @DisplayName("지원하지 않는 값으로 FileExtension 생성한다.")
    @Test
    void createWithInvalidValue() {
        assertThatThrownBy(() -> FileExtension.from("ggyool"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("지원하는 확장자가 아닙니다");
    }

    @DisplayName("FileExtension 추출한다.")
    @Test
    void extractExtension() {
        String path = "/css/style.css";
        FileExtension fileExtension = FileExtension.extractExtension(path);
        assertThat(fileExtension).isSameAs(FileExtension.CSS);
    }

    @DisplayName("경로의 파일이 지원하는 FileExtension 이면 참, 아니면 거짓을 반환한다.")
    @Test
    void supports() {
        String validExtension = "/css/favicon.ico";
        String invalidExtension = "/css/favicon.ggyool";

        assertThat(FileExtension.supports(validExtension)).isTrue();
        assertThat(FileExtension.supports(invalidExtension)).isFalse();
    }

    @DisplayName("확장자가 일치하면 참을 반환한다.")
    @Test
    void match() {
        String fileName = "/js/script.js";
        FileExtension fileExtension = FileExtension.extractExtension(fileName);
        assertThat(fileExtension.match(FileExtension.JS)).isTrue();
    }
}
