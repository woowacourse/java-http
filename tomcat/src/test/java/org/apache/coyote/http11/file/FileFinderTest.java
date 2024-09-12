package org.apache.coyote.http11.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileFinderTest {

    @DisplayName("파일 상세 정보를 통해 파일의 모든 줄을 읽는다.")
    @Test
    void resolve() throws IOException {
        //given
        FileDetails fileDetails = FileDetails.from("/login");
        FileFinder fileFinder = new FileFinder(fileDetails);

        //when
        String result = fileFinder.resolve();

        //then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        File file = new File(Objects.requireNonNull(resource).getFile());
        String expected = new String(Files.readAllBytes(file.toPath()));
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("파일 상세 정보와 동일한 정적 파일이 존재하지 않으면 예외가 발생한다.")
    @Test
    void resolveThrowsException() {
        //given
        FileDetails fileDetails = FileDetails.from("/gugu");
        FileFinder fileFinder = new FileFinder(fileDetails);

        //when //then
        assertThatThrownBy(fileFinder::resolve)
                .isInstanceOf(NoResourceFoundException.class)
                .hasMessageContaining(fileDetails.getFilePath() + "파일이 존재하지 않습니다.");
    }
}
