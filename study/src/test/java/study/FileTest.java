package study;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

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
// 파일 시스템 기준 접근
//        final String filePath = "src/test/resources/" + fileName;
//        final File file = new File(filePath);
//        SoftAssertions.assertSoftly(softAssertions -> {
//            softAssertions.assertThat(file.exists()).isTrue();
//            softAssertions.assertThat(file.toString()).endsWith(fileName);
//        });

// 리소스의 위치를 표현하는 객체
        final URL url = getClass().getClassLoader().getResource(fileName);
//        // file:/C:/Users/User/Desktop/tomcat_mission/java-http/study/build/resources/test/nextstep.txt
//        // URL은 네트워크 리소스를 표현하기 위한 일반화된 객체
//        System.out.println(url.toString());
//        // /C:/Users/User/Desktop/tomcat_mission/java-http/study/build/resources/test/nextstep.txt
//        // OS 경로처럼 쓰고 싶을 때
//        System.out.println(url.getPath());
//        // /C:/Users/User/Desktop/tomcat_mission/java-http/study/build/resources/test/nextstep.txt
//        // path + query가 필요할 때
//        System.out.println(url.getFile());
        assertThat(url.toString()).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     *
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws URISyntaxException, IOException {
        final String fileName = "nextstep.txt";
        final String expected = "nextstep";

//        Files 클래스 사용
//        final Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
//        final List<String> actual = Files.readAllLines(path);

//        assertThat(actual).containsOnly(expected);

        //File 클래스 사용
        final Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
        final File file = path.toFile();

        //FileReader가 Reader 상속받아서, BufferedReader에 fileReader 주입 가능
        try (final FileReader fileReader = new FileReader(file)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> actual = bufferedReader.lines()
                    .toList();

            assertThat(actual).containsOnly(expected);
        }
    }
}
