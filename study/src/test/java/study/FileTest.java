package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
     * <p>
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.
     * 자바 애플리케이션은 resource 디렉터리에 HTML, CSS 같은 정적 파일을 저장한다.
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        //현재 프로젝트의 경로를 알아내는 방법은...? -> 해결
        // 현재 위치하고 있는 곳 (test / main ) 을 알아내는 방법은?

        final String fileName = "nextstep.txt";

        // todo
        /*List<File> resoursesFiles = getResourceFile();
        final String actual = resoursesFiles.stream()
                .flatMap(resoursesFile -> Arrays.stream(resoursesFile.listFiles()))
                .filter(file -> file.getName().equals(fileName))
                .findFirst()
                .orElseThrow()
                .getName();*/

        /*File resources = new File("/Users/mac/Desktop/zangsu/wooteco/Level4/java-http/study/src/test/resources");
        final String actual = Arrays.stream(resources.listFiles())
                .filter(file -> file.getName().equals(fileName))
                .findFirst()
                .orElseThrow()
                .getName();*/

        // 세상에 몰리 대박
        final URL resoucrcesUrl = getClass().getClassLoader().getResource(fileName);
        String actual = resoucrcesUrl.getFile();

        assertThat(actual).endsWith(fileName);
    }

    private List<File> getResourceFile() {
        File file = new File(".").getAbsoluteFile();

        List<File> results = new ArrayList<>();
        addResourcesFile(file, results);
        Arrays.stream(file.listFiles())
                .filter(subFile -> subFile.getName().equals("resources"))
                .forEach(subFile -> results.add(subFile));
        return results;
    }

    private void addResourcesFile(File parent, List<File> results) {
        for(File file : parent.listFiles()) {
            if (file.isFile() && file.getName().equals("resources")) {
                results.add(file);
            } else {
                addResourcesFile(file, results);
            }
        }
    }

    private File getCurrentProject() {
        Path path = Paths.get("");
        File file = path.toAbsolutePath().toFile();
        System.out.println("file = " + file);
        return file;
    }

    /**
     * 파일 내용 읽기
     * <p>
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() {
        final String fileName = "nextstep.txt";

        // todo
        final Path path = null;

        // todo
        final List<String> actual = Collections.emptyList();

        assertThat(actual).containsOnly("nextstep");
    }
}
