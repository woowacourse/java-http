package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("File 클래스 학습 테스트")
class FileTest {

    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() throws URISyntaxException {
        final String fileName = "nextstep.txt";

        final URL resource = getClass().getClassLoader().getResource(fileName);
        final File file = Paths.get(resource.toURI()).toFile();

        final String actual = file.getName();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다. File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws URISyntaxException, IOException {
        final String fileName = "nextstep.txt";

        // todo
        final URL resource = getClass().getClassLoader().getResource(fileName);
        final Path path = Paths.get(resource.toURI());


         BufferedReader br = Files.newBufferedReader(path);

        // todo
        final List<String> actual = br.lines().collect(Collectors.toList());

        assertThat(actual).containsOnly("nextstep");
    }
}
