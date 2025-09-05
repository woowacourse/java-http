package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        /*
        클래스 로더를 이용하면 컴파일된 클래스뿐만 아니라 리소스 파일들도 접근할 수 있게 해준다.
        클래스 로더 :
        자바의 클래스 로더(Java ClassLoader)는 자바 클래스 파일(.class)과 리소스 파일을 자바 가상 머신(JVM)으로 동적으로 읽어들이고 로드하는 역할을 한다.
        클래스가 처음 필요할 때 메모리에 적재하고, 메모리에 한번 로드된 클래스는 다시 로드하지 않도록 중복 방지 기능도 한다.
        자바는 컴파일 타임이 아니라 런타임에 클래스를 동적으로 로드하는데, 이 동적 로드를 담당하는 것이 클래스 로더다.
         */
        final String actual = getClass().getClassLoader().getResource(fileName).getPath();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     * <p>
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다. File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws IOException {
        final String fileName = "nextstep.txt";

        /*
        리소스 파일을 Path로 읽어오는 경우
        리소스 파일이 빌드 후 JAR 파일 내부에 포함된 경우, 파일 시스템 상의 실제 경로(Path)를 얻는 것이 불가능하거나 위험하다.
         */
        // final URL resoucrcesUrl = getClass().getClassLoader().getResource(fileName);
        // final List<String> actual = Files.readAllLines(Paths.get(resoucrcesUrl.getPath()));

        /*
         리소스 디렉터리에 있는 파일을 InputStream 형태로 읽음
         파일을 바로 경로나 File 객체로 접근하지 않고 데이터 스트림으로 다룰 때 주로 쓴다. 리소스가 JAR 내부에 있을 때도 문제없이 읽을 수 있다.
         */
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(fileName);

        // 스트림에서 내용을 읽어 문자열로 변환
        List<String> actual = new BufferedReader(new InputStreamReader(resourceStream))
                .lines()
                .toList();

        assertThat(actual).containsOnly("nextstep");
    }
}
