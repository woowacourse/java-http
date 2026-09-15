package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 웹서버는 사용자가 요청한 html 파일을 제공 할 수 있어야 한다.
 * File 클래스를 사용해서 파일을 읽어오고, 사용자에게 전달한다.
 */
@DisplayName("File 클래스 학습 테스트")
class FileTest {

    /**
     * resource 디렉터리 경로 찾기
     * <p>
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.
     * 자바 애플리케이션은 resource 디렉터리에 HTML, CSS 같은 정적 파일을 저장한다.
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        // given
        final String fileName = "nextstep.txt";

        // when
        final String actual = getClass().getClassLoader().getResource(fileName).getPath();

        // then
        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     * <p>
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void Files를_사용해_파일의_내용을_읽는다() throws Exception {
        // given
        final String fileName = "nextstep.txt";
        Path file = Path.of(getClass().getClassLoader().getResource(fileName).toURI());

        // when
        final List<String> actual = Files.readAllLines(file, StandardCharsets.UTF_8);

        // then
        assertThat(actual).containsOnly("nextstep");
    }

    @Test
    void File과_Stream를_사용해_파일의_내용을_읽는다() throws Exception {
        // given
        final String fileName = "nextstep.txt";
        final File file = new File(getClass().getClassLoader().getResource(fileName).toURI());

        // File을 기반으로 BufferedReader 스트림 생성
        final List<String> actual;
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            actual = reader.lines().toList();
        }

        assertThat(actual).containsOnly("nextstep");
    }

}
