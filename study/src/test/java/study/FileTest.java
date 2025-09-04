package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
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
        final File file = new File("/Users/ichaeyeong/Desktop/woowacourse/level3/java-http/study/src/test/resources", fileName);
        final String actual = file.getAbsolutePath();

        // 파일을 읽을 수 있다.
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     * <p>
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws URISyntaxException, IOException {
        final String fileName = "nextstep.txt";

        // todo
        /*
         file의 경로여야 한다. (디렉토리면 안됨)
         상대경로로 두면 아예 경로를 찾지 못한다 - 절대경로로 인식하는 듯하다.
         */
        /*
        Path.of나 Paths.get을 통해 경로를 조각조각 붙일 수 있다.
         */
        final Path path = Path.of("/Users/ichaeyeong/Desktop/woowacourse/level3/java-http/study/src/test/resources", fileName);

        // todo
        final List<String> actual = Files.readAllLines(path);

        // File을 대상으로 CRUD할 수 있다.
        final Path newPath = Path.of("/Users/ichaeyeong/Desktop/woowacourse/level3/java-http/study/src/test/resources", "copy.txt");
        Files.copy(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        Files.writeString(newPath, "copy of nextstep");

        assertThat(actual).containsOnly("nextstep");
    }
}
