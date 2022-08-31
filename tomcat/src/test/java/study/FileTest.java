package study;

import static org.assertj.core.api.Assertions.assertThat;

import static study.Resource.newResource;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("File 클래스 학습 테스트")
class FileTest {

    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        final String fileName = "nextstep.txt";

        final String actual = newResource(fileName).getPath();
        assertThat(actual).endsWith(fileName);
    }

    @Test
    void 파일의_내용을_읽는다() {
        final String fileName = "nextstep.txt";

        final List<String> actual = newResource(fileName).read();

        assertThat(actual).containsOnly("nextstep");
    }
}
