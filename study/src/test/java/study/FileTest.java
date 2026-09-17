package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
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
     * <p>
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.
     * 자바 애플리케이션은 resource 디렉터리에 HTML, CSS 같은 정적 파일을 저장한다.
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        final String fileName = "nextstep.txt";

        // todo
        // 상대 경로의 기준이 되는 현재 작업 디렉터리를 출력한다.
        System.out.println(System.getProperty("user.dir"));
        // 현재 작업 디렉터리를 기준으로 nextstep.txt의 상대 경로를 표현하는 File 객체를 만든다.
        File file = new File("src/test/resources/nextstep.txt");
        // File 객체가 나타내는 경로에서 마지막 파일 이름만 가져온다.
        String actual = file.getName();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     * <p>
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws IOException {
        final String fileName = "nextstep.txt";

        // todo
        // 현재 작업 디렉터리를 기준으로 읽을 파일의 상대 경로를 표현한다.
        File file = new File("src/test/resources/nextstep.txt");
        // File 객체를 Files 클래스에서 사용할 수 있는 Path 객체로 변환한다.
        final Path path = file.toPath();

        // todo
        // Path가 가리키는 파일을 줄 단위로 모두 읽어 List<String>에 저장한다.
        final List<String> actual = Files.readAllLines(path);

        assertThat(actual).containsOnly("nextstep");
    }
}
