package study;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 웹서버는 사용자가 요청한 html 파일을 제공 할 수 있어야 한다.
 * File 클래스를 사용해서 파일을 읽어오고, 사용자에게 전달한다.
 */
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("File 클래스 학습 테스트")
class FileTest {

    /**
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.
     * 자바 애플리케이션은 resource 디렉터리에 정적 파일을 저장한다.
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    @Nested
    class resource_디렉터리에_있는_파일의_경로를_찾는다 {

        @Test
        void ClassLoader() throws URISyntaxException {
            final String fileName = "nextstep.txt";

            final ClassLoader classLoader = this.getClass().getClassLoader();
            final URI resourceUri = classLoader.getResource(fileName).toURI();
            final String actual = resourceUri.getPath(); // /Users/jeong/.../jwp-dashboard-http/tomcat/build/resources/test/nextstep.txt

            assertThat(actual).endsWith(fileName);
        }

        @Test
        void Paths() {
            final String fileName = "nextstep.txt";

            final Path filePath = Paths.get("src", "test", "resources", fileName);
            final String actual = filePath.toFile().getAbsolutePath();

            assertThat(filePath.endsWith(fileName)).isTrue();
            assertThat(actual).endsWith("src/test/resources/" + fileName);
        }
    }

    /**
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Nested
    class 파일의_내용을_읽는다 {

        @Test
        void Files() throws IOException {
            final String fileName = "nextstep.txt";

            final Path path = Paths.get("src", "test", "resources", fileName);
            final List<String> actual = Files.lines(path, StandardCharsets.UTF_8)
                    .collect(Collectors.toList());

            assertThat(actual).containsOnly("nextstep");
        }

        @Test
        void File() throws IOException {
            final String fileName = "nextstep.txt";
            ClassLoader classLoader = this.getClass().getClassLoader();

            final File file= new File(classLoader.getResource(fileName).getFile());
            try (final InputStream inputStream = new FileInputStream(file);
                 final InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 final BufferedReader reader = new BufferedReader(streamReader)) {
                final List<String> actual = reader.lines().collect(Collectors.toList());
                assertThat(actual).containsOnly("nextstep");
            }
        }
    }
}
