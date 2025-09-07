package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
     * 그러게요. 어떻게 찾을까? -> 스프링이 아닌 자바 내에서 찾는 방법... 뭘까. 진짜 모름
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다_1() throws URISyntaxException {
        final String fileName = "nextstep.txt";
        Class<? extends FileTest> classOfTest = this.getClass();
        URL resource = classOfTest.getResource("/" + fileName);
        Path path = Paths.get(resource.toURI());
        final String actual = path.toAbsolutePath().toString();

        assertThat(actual).endsWith(fileName);
    }

    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다_2() throws URISyntaxException {
        final String fileName = "nextstep.txt";
        URL resource = ClassLoader.getSystemClassLoader().getResource(fileName);
        Path path = Paths.get(resource.toURI());
        final String actual = path.toAbsolutePath().toString();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     *
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다_1() throws URISyntaxException, IOException {
        final String fileName = "nextstep.txt";

        Class<? extends FileTest> classOfTest = this.getClass();
        URL resource = classOfTest.getResource("/" + fileName);
        URI uri = resource.toURI();
        Path path = Paths.get(uri);
        final List<String> actual = Files.readAllLines(path);

        assertThat(actual).containsOnly("nextstep");
    }

    @Test
    void 파일의_내용을_읽는다_2() throws IOException {
        final String fileName = "nextstep.txt";
        // 최종 결과를 담을 List
        List<String> actual = new ArrayList<>();
        try (
                // 1. 클래스패스에서 리소스를 바이트 스트림(InputStream)으로 얻기
                InputStream inputStream = getClass().getResourceAsStream("/" + fileName);
                // 2. 바이트 스트림을 문자 스트림(Reader)으로 변환 (인코딩 지정)
                InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                // 3. 문자 스트림을 버퍼를 사용해 한 줄씩 읽도록(BufferedReader) 감싸기
                BufferedReader reader = new BufferedReader(streamReader)
        ) {
            String line;
            // 4. 한 줄씩 읽어 null이 아닐 때까지 반복
            while ((line = reader.readLine()) != null) {
                actual.add(line);
            }
        }

        assertThat(actual).containsOnly("nextstep");
    }
}
