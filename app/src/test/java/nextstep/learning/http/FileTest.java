package nextstep.learning.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 웹서버는 사용자가 요청한 html 파일을 제공 할 수 있어야 한다.<br>
 * File 클래스를 사용해서 파일을 읽어오고, 사용자에게 전달한다.
 */
@DisplayName("File 클래스 학습 테스트")
class FileTest {

    /**
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.<br>
     * 자바 애플리케이션은 resource 디렉터리에 정적 파일을 저장한다.<br>
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     * A: ClassLoader의 getResource를 이용해서, 컴파일 되고 빌드된 resources에서 찾는다. (?)
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        final String fileName = "nextstep.txt";

        final URL resource = getClass().getClassLoader().getResource("nextstep.txt");
        final String actual = resource.getFile();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     * A: ClassLoader의 getResource를 이용해서, 컴파일 되고 빌드된 resource를 찾고 파일로 만들어서 readAllLines 사용.
     */
    @Test
    void 파일의_내용을_읽는다() throws IOException {
        final String fileName = "nextstep.txt";

        final URL fileResourceURL = getClass().getClassLoader().getResource(fileName);
        final Path path = Paths.get(fileResourceURL.getPath());

        final List<String> actual = Files.readAllLines(path);

        assertThat(actual).containsOnly("nextstep");
    }

    @Test
    void 파일의_내용을_읽는다_InputStream_학습테스트() throws IOException {
        final String fileName = "nextstep.txt";

        final URL fileResourceURL = getClass().getClassLoader().getResource(fileName);
        final Path path = Paths.get(fileResourceURL.getPath());

        InputStream fileInputStream = new FileInputStream(path.toFile());

        byte[] bytes = fileInputStream.readAllBytes();
        String actual = new String(bytes);

        assertThat(actual).isEqualTo("nextstep");
    }

    @Test
    void 파일의_내용을_읽는다_BufferedReader_학습테스트() throws IOException {
        final String fileName = "nextstep.txt";

        final URL fileResourceURL = getClass().getClassLoader().getResource(fileName);
        final Path path = Paths.get(fileResourceURL.getPath());

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile())));
        List<String> actual = new ArrayList<>();

        for (;;) {
            String line = br.readLine();
            if (line == null)
                break;
            actual.add(line);
        }

        assertThat(actual).containsOnly("nextstep");
    }
}
