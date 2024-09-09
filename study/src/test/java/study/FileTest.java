package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

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
     *
     *
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.
     * 자바 애플리케이션은 resource 디렉터리에 HTML, CSS 같은 정적 파일을 저장한다.
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    /**
     * 자바 실행 시
     *
     * 1. 자바 컴파일러는 소스코드(.java)를 바이트 코드로 변환(.class)하여 저장한다.
     * 2. 클래스 로더를 통해 프로그램에서 참조되는 모든 .class 파일들이 로딩된다.
     * 3. 링킹, 초기화 과정에 거쳐 JVM은 main 메서드를 호출해서 프로그램을 실행한다.
     */
    /**
     * 클래스패스
     *
     * Java 애플리케이션이 실행될 때, JVM이나 컴파일러가 클래스파일이나 리소스 파일을 찾는데 사용되는 경로 목록이다.
     * 자바 클래스패스에서 어디에 위치해 있는지를 나타내는 경로가 URL이다.
     */
    /**
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     *
     * 클래스로더로 클래스패스를 통해 resource 디렉토리 내 파일명으로 경로를 찾을 수 있다.
     *
     * 리소스 파일의 배포
     * 애플리케이션이 배포될 때, 리소스 파일은 JAR/WAR 파일에 포함되어 배포된다.
     * 이러한 리소스 파일들은 애플리케이션이 실행되는 동안 정적 콘텐츠로서 사용된다.
     * resource의 파일들은 빌드 과정에서 target/classes나 build/classes 디렉토리로 복사된다.
     * 복사된 리소스 파일은 JVM의 클래스패스에 포함되어 애플리케이션에서 접근할 수 있다.
     * JVM은 클래스 로더를 통해 클래스 파일에 포함된 리소스 파일을 검색하고, InputStream이나 URL 객체로 반환하여 일긍ㄹ 수 있게 한다.
     */
    /**
     * 왜 클래스패스로 접근해야할까?
     * <p>
     * "환경에 관계없이" "특정 경로에 종속되지 않고" resource의 경로를 구하기 위해서이다.
     * 파일 시스템 경로로 접근할 경우, 환경마다 파일 경로가 다르므로 오류가 발생할 수 있다.
     * <p>
     * 클래스패스를 통해 파일을 접근하면 파일 시스템의 절대 경로와 상관없이 모든 환경에서 동일한 방식으로 파일에 접근할 수 있다.
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        final String fileName = "nextstep.txt";

        String actual = getClass().getClassLoader().getResource(fileName).getPath();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     *
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    /**
     * Path.of() VS new File().toPath()
     *
     * <code>getResource(fileName).getPath()</code>는 파일의 URL 경로를 반환하는데, 인코딩으로 인해 공백이나 기타 특수 문자가 `%20`과 같은 형식으로 변환될 수 있다.
     *
     * <code>Path.of()</code>는 URL 인코딩이 적용된 경로를 제대로 처리하지 못할 수 있다. 따라서 파일을 찾지 못하고, 예외가 발생할 수 있다.
     * 따라서 URL 경로의 인코딩 문제를 피하려면 toURI()를 사용하여 Path 객체를 생성하는 것이 안전하다.
     *
     * <code>new File().toPath()</code>는
     *  1. File 객체를 생성할 때 URL 경로를 적절히 디코딩하여 실제 파일 시스템 경로로 변환한다.
     *  2. toPath() 메서드는 이 file 객체의 경로를 Path 객체로 변환한다.
     * 이 과정에서 URL 인코딩된 부분이 정상적인 파일 시스템 경로로 디코딩되므로 파일을 제대로 찾을 수 있다.
     */
    /**
     * Files.line() VS Files.readAllLines()
     * <p>
     * Files.line()은 모든 줄을 스트림으로 반환한다.
     * 파일의 내용을 지연로딩 방식으로 읽어온다.
     * 필요한 순간에 한줄씩 읽어오므로 대용량 파일 처리를 하거나, 스트림 API를 활용할 때 이점을 누릴 수 있다.
     * <p>
     * Files.readAllLines()는 모든 줄을 한번에 읽어 리스트 형태로 반환한다.
     * 파일의 모든 내용을 한꺼번에 메모리에 로드하므로, 메모리에 파일의 모든 내용이 저장된다.
     * 따라서 접근이 빠르지만, 파일이 클 경우 메모리 사용량이 많아져 메모리 부족 문제가 발생하거나 성능이 저하될 수 있다.
     */
    @Test
    void 파일의_내용을_읽는다() throws IOException {
        final String fileName = "nextstep.txt";

        //Path path = Path.of(getClass().getClassLoader().getResource(fileName).getPath()); -> (X) : java.nio.file.InvalidPathException: Illegal char <:> at index 2:
        //Path path = Path.of(getClass().getClassLoader().getResource(fileName).toURI()); -> (O)
        Path path = new File(getClass().getClassLoader().getResource(fileName).getPath()).toPath();
        List<String> actual;
        try (Stream<String> lines = Files.lines(path)) {
            actual = lines.toList();
        }

        actual.forEach(System.out::println);

        assertThat(actual).containsOnly("nextstep");
    }
}
