package nextstep.joanne.converter;

import nextstep.joanne.exception.FileNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileConverterTest {

    @Test
    void getResource() {
        String uri = "/index.html";
        String resource = FileConverter.getResource(uri);
        assertThat(resource).isNotNull();
    }

    @Test
    @DisplayName("File Not Found")
    void getResourceFileIOError() {
        String uri = ".joanne";
        assertThatThrownBy(() -> FileConverter.getResource(uri))
                .isInstanceOf(FileNotFoundException.class);
    }
}