package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        File file = new File(resource.getFile());

        final String actual = file.getName();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     * <p>
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다. File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws URISyntaxException, IOException {
        final String fileName = "nextstep.txt";

        // todo
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        Path path = Paths.get(resource.toURI());

        // todo
        final List<String> actual = Files.readAllLines(path);

        assertThat(actual).containsOnly("nextstep");
    }
    
    /*
    더욱 학습해볼만한 포인트
    - ClassLoader란?
        : 클래스 파일을 메모리에 로드하는 역할하는 Java의 핵심 구성 요소
        : 주요 역할 로는 "리소스 로딩"과 "클래스 로딩"이 있다.
        : 리소스 로딩 = 클래스패스에 있는 리소스 파일들(.class 파일, resources의 파일, ...)에 접근
        : 클레스 로딩 = 리소스 로딩을 통해 적절한 .class파일을 찾아 JVM의 메모리에 로드
    - ClassLoader를 활용하여 resource 디렉토리의 URI 구하기
        : 학습 테스트 참조
    - File의 다양한 활용법
    - Files의 다양한 활용법
    - Path의 다양한 활용법
     */
}
