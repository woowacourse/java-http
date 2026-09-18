package study;

import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;
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

        // todo
        // 스프링 자바 애플리케이션은 resource 디렉터리에 있는 파일을 ClassLoader를 사용해서 찾는다.
        // 이는 실행 시 classPath에 있는 파일을 찾는 방법이다.
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(fileName);
        final String actual = url.toString();

        // 다른 방법으로는 파일 객체를 생성해서 getAbsolutePath() 메서드를 사용해서 절대 경로를 찾을 수 있다.
        // 단 이 방법은 운영체제 실제 경로를 반환하기 때문에, jar 파일로 패키징된 경우에는 사용할 수 없다.
        // fileName = "src/main/resources/nextstep.txt";
        // final File file = new File(fileName);
        // final String actual = file.getAbsolutePath();

        assertThat(actual).endsWith(fileName);
        assertThat(actual).endsWith("/java-http/study/build/resources/test/nextstep.txt");
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

        // todo
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(fileName);
        Assertions.assertNotNull(url);
        final Path path = Path.of(url.getPath());

        // todo
        final List<String> actual = new ArrayList<>();
        try(FileReader fileReader = new FileReader(path.toFile())) {
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = fileReader.read()) != -1) {
                sb.append((char) ch);
            }
            actual.add(sb.toString().trim());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        assertThat(actual).containsOnly("nextstep");

    }
}
