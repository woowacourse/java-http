package nextstep.joanne.server.converter;

import nextstep.joanne.dashboard.exception.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileConverterTest {

    FileConverter fileConverter;

    @BeforeEach
    void setUp() {
        fileConverter = new FileConverter();
    }

    @Test
    void getResource() {
        String uri = "/index.html";
        String resource = fileConverter.getResource(uri);
        assertThat(resource).isNotNull();
    }

    @Test
    @DisplayName("File Not Found")
    void getResourceFileIOError() {
        String uri = ".joanne";
        assertThatThrownBy(() -> fileConverter.getResource(uri))
                .isInstanceOf(FileNotFoundException.class);
    }
}