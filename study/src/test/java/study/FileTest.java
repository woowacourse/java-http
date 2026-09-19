package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 웹서버는 사용자가 요청한 html 파일을 제공 할 수 있어야 한다.
 * File 클래스를 사용해서 파일을 읽어오고, 사용자에게 전달한다.
 */
@DisplayName("File 클래스 학습 테스트")
class FileTest {

    /**
     * resource 디렉터리 경로 찾기
     *
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.
     * 자바 애플리케이션은 resource 디렉터리에 HTML, CSS 같은 정적 파일을 저장한다.
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    @Test
    @DisplayName("클래스 로더로 리소스 파일의 경로를 찾는다")
    void findsResourcePathUsingClassLoader() throws URISyntaxException {
        // given
        final String fileName = "nextstep.txt";

        // when
        final var resource = Objects.requireNonNull(
                getClass().getClassLoader().getResource(fileName), "테스트 리소스를 찾을 수 없습니다: " + fileName);
        final String actual = Path.of(resource.toURI()).toString();

        // then
        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     *
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    @DisplayName("리소스 파일의 내용을 줄 단위로 읽는다")
    void readsResourceFileLineByLine() throws IOException, URISyntaxException {
        // given
        final String fileName = "nextstep.txt";
        final var resource = Objects.requireNonNull(
                getClass().getClassLoader().getResource(fileName), "테스트 리소스를 찾을 수 없습니다: " + fileName);
        final Path path = Path.of(resource.toURI());

        // when
        final List<String> actual = Files.readAllLines(path);

        // then
        assertThat(actual).containsExactly("nextstep");
    }
}
