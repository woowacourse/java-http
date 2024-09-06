package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 웹서버는 사용자가 요청한 html 파일을 제공 할 수 있어야 한다. File 클래스를 사용해서 파일을 읽어오고, 사용자에게 전달한다.
 */
@DisplayName("File 클래스 학습 테스트")
class FileTest {

    /**
     * resource 디렉터리 경로 찾기
     * <p>
     * File 객체를 생성하려면 파일의 경로를 알아야 한다. 자바 애플리케이션은 resource 디렉터리에 HTML, CSS 같은 정적 파일을 저장한다. resource 디렉터리의 경로는 어떻게 알아낼 수
     * 있을까?
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        final String fileName = "nextstep.txt";

        // todo
        String actual = "";
        try {
            // 리소스 파일의 File 객체 얻기
            File resourceFile = getResourceFile(fileName);
            actual = resourceFile.getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        assertThat(actual).endsWith(fileName);
    }

    private File getResourceFile(final String resourceName) throws URISyntaxException {
        final URL resource = getClass().getResource("/" + resourceName);
        return Paths.get(resource.toURI()).toFile();
    }

    /**
     * 파일 내용 읽기
     * <p>
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다. File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() {
        final String fileName = "nextstep.txt";

        // todo
        final List<String> actual = new java.util.ArrayList<>(Collections.emptyList());
        try {
            final File file = getResourceFile(fileName);
            actual.addAll(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        assertThat(actual).containsOnly("nextstep");
    }
}
