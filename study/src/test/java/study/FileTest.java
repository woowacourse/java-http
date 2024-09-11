package study;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
        final Class<? extends FileTest> aClass = getClass();
        System.out.println(aClass);
        System.out.println(aClass.getClassLoader());
        System.out.println(aClass.getClassLoader().getResource(fileName));

        final String actual = getClass().getClassLoader().getResource(fileName).getPath();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 파일 내용 읽기
     *
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws IOException {
        final String fileName = "nextstep.txt";
        final String fileName2 = "nextstep2.txt";

        final String filePath = getClass().getClassLoader().getResource(fileName).getPath();
        final String filePath2 = getClass().getClassLoader().getResource(fileName2).getPath();

        File file = new File(filePath);
        File file2 = new File(filePath2);

        // todo
        final Path path = file.toPath();
        final Path path2 = file2.toPath();

        // todo
        final List<String> actual = Files.readAllLines(path);
        final List<String> actual2 = Files.readAllLines(path2);

        System.out.println("actual은 " + actual);
        System.out.println("actual2은 " + actual2);

        assertThat(actual).containsOnly("nextstep");
        assertThat(actual2).containsOnly("nextstep1", "nextstep2");
    }
}
