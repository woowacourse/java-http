package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
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
     *
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.
     * 자바 애플리케이션은 resource 디렉터리에 HTML, CSS 같은 정적 파일을 저장한다.
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        final String fileName = "nextstep.txt";

        /* [1] Class 사용 :
            Class는 자신이 속한 패키지를 기준(상대 경로)으로 소스를 찾는 것을 기본값으로 하므로
            상대 경로와 절대 경로를 구분할 수 있는 장치가 필요한데, 그 역할을 /가 해준다!
            Class의 getResource 메서드는 내부적으로는 ClassLoader에게 일을 위임하는 방식을 채택하고 있다.
            /가 붙으면 /를 뺀 이름을 ClassLoader에게 주면서 경로를 찾도록 하고,
            /가 없으면 현재 클래스의 패키지 경로(예: com/example/)를 문자열 앞에 붙여서 ClassLoader에게 넘겨준다.
        */
        // final String actual = getClass().getResource("/" + fileName).getPath();

        // [2] ClassLoader 사용 : 클래스패스(Classpath)의 최상위 루트만을 유일한 시작점으로 인식하므로, 오히려 /를 붙이면 동작에 오류가 생김!
        final String actual = getClass().getClassLoader().getResource(fileName).getPath();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     *
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() {
        final String fileName = "nextstep.txt";

        final String resourceURL = ClassLoader.getSystemClassLoader().getResource(fileName).getPath();
        final Path path = Path.of(resourceURL);

        try (final BufferedReader reader = Files.newBufferedReader(path)) {
            final List<String> actual = reader.lines().toList();
            assertThat(actual).containsOnly("nextstep");
        } catch (IOException e) {
            System.err.println("입출력 시스템 오류 발생");
        }
    }
}
