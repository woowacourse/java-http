package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * File 클래스와 파일 입출력을 단계별로 학습하는 테스트 각 단계를 하나씩 해결하면서 파일 입출력에 대한 이해를 높여보자.
 */
@DisplayName("File 클래스 단계별 학습 테스트")
class FileStepByStepTest {

    /**
     * 0단계: 테스트 실행 환경 이해하기
     * <p>
     * Gradle 모듈에서 테스트가 실행되는 위치를 이해해보자. - 현재 작업 디렉토리 확인
     */
    @Test
    void step0_테스트_실행_환경_이해하기() {
        // 현재 작업 디렉토리를 확인해보자
        String currentDir = System.getProperty("user.dir");
        assertThat(currentDir).endsWith("study");
    }

    /**
     * 1단계: File 객체 생성과 기본 정보 확인
     * <p>
     * File 클래스의 기본적인 사용법을 익혀보자. - File 객체 생성 - 파일 존재 여부 확인 - 파일 크기 확인 - 올바른 상대 경로 사용
     */
    @Test
    void step1_File_객체_생성과_기본정보_확인() {
        // 현재 작업 디렉토리를 기준으로 올바른 상대 경로를 사용해보자
        String currentDir = System.getProperty("user.dir");
        System.out.println("currentDir: " + currentDir);
        assertThat(currentDir).endsWith("study");

        // 현재 작업 디렉토리를 기준으로 파일을 찾을 수 있다.
        File file1 = new File("src/test/resources/nextstep.txt");
        assertThat(file1.exists()).isTrue();

        // 테스트 파일을 기준으로 상대 경로를 설정할 경우 파일을 찾을 수 없다.
        File file2 = new File("../../resources/nextstep.txt");
        assertThat(file2.exists()).isFalse();

        // 파일의 크기를 확인해보자
        long size = file1.length();
        System.out.println("size: " + size);
        assertThat(size).isGreaterThan(0);

        // 절대 경로도 확인해보자
        String absolutePath = file1.getAbsolutePath();
        System.out.println("absolutePath: " + absolutePath);
        assertThat(absolutePath).contains("nextstep.txt");
    }

    /**
     * 2단계: Path 객체 사용하기
     * <p>
     * Java 7부터 도입된 Path 인터페이스를 사용해보자. - Paths.get() 사용법 - Path의 메서드들 - 상대 경로와 절대 경로
     */
    @Test
    void step2_Path_객체_사용하기() {
        // Paths.get()을 사용해서 Path 객체를 생성해보자
        Path path = Paths.get("src/test/resources/nextstep.txt");

        // Path의 파일명을 가져와보자
        String fileName = path.getFileName().toString();
        assertThat(fileName).isEqualTo("nextstep.txt");

        // Path의 부모 디렉토리를 가져와보자
        Path parent = path.getParent();
        assertThat(parent.getFileName().toString()).isEqualTo("resources");

        // 절대 경로로 변환해보자
        Path absolutePath = path.toAbsolutePath();
        assertThat(absolutePath).isAbsolute();
    }

    /**
     * 3단계: 클래스패스에서 리소스 찾기
     * <p>
     * 클래스패스에 있는 리소스 파일의 경로를 찾는 방법을 익혀보자. - ClassLoader 사용 - getResource() 메서드 - 리소스 URL을 파일 경로로 변환
     */
    @Test
    void step3_클래스패스에서_리소스_찾기() {
        // ClassLoader를 사용해서 리소스를 찾아보자
        ClassLoader classLoader = getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource("nextstep.txt");
        assertThat(resourceUrl).isNotNull();

        // 리소스 URL에서 파일 경로를 가져와보자
        String filePath = resourceUrl.getFile();
        assertThat(filePath).contains("nextstep.txt");

        // URL을 Path 객체로 변환해보자
        Path path = Paths.get(filePath);
        assertThat(Files.exists(path)).isTrue();
    }

    /**
     * 4단계: Files.readAllLines() 사용하기
     * <p>
     * Files 클래스의 편리한 메서드를 사용해서 파일 내용을 읽어보자. - Files.readAllLines() - 예외 처리 - 다양한 경로 방식으로 파일 읽기
     */
    @Test
    void step4_Files_readAllLines_사용하기() throws IOException {
        // 방법 1: 상대 경로로 Path 객체를 생성해보자
        Path relativePath = Paths.get("src/test/resources/nextstep.txt");

        // 방법 2: ClassLoader로 리소스를 찾아서 Path 생성
        URL resourceUrl = getClass().getClassLoader().getResource("nextstep.txt");
        Path resourcePath = Paths.get(resourceUrl.getFile());

        // 두 방법 모두로 파일 내용을 읽어보자
        List<String> relativeLines = Files.readAllLines(relativePath);
        List<String> resourceLines = Files.readAllLines(resourcePath);

        assertThat(relativeLines).isNotEmpty();
        assertThat(relativeLines.getFirst()).isEqualTo("nextstep");
        assertThat(resourceLines).isNotEmpty();
        assertThat(resourceLines.getFirst()).isEqualTo("nextstep");
    }

    /**
     * 5단계: Files.readString() 사용하기 (Java 11+)
     * <p>
     * Java 11부터 제공되는 Files.readString()을 사용해보자. - 전체 파일을 하나의 String으로 읽기 - 다양한 경로 방식으로 파일 읽기
     */
    @Test
    void step5_Files_readString_사용하기() throws IOException {
        // Path 객체를 생성해보자
        Path path = Paths.get("src/test/resources/nextstep.txt");

        // Files.readString()을 사용해서 파일 내용을 읽어보자
        String content = Files.readString(path);

        assertThat(content).contains("nextstep");
        assertThat(content).isNotEmpty();
    }

    /**
     * 6단계: 파일 존재 여부와 읽기 권한 확인
     * <p>
     * 파일을 읽기 전에 필요한 검증을 해보자. - 파일 존재 여부 - 읽기 권한 - 다양한 경로 방식으로 검증
     */
    @Test
    void step6_파일_존재여부와_읽기권한_확인() {
        // 상대 경로로 Path 객체를 생성해보자
        Path relativePath = Paths.get("src/test/resources/nextstep.txt");

        // ClassLoader로 리소스를 찾아서 Path 생성
        URL resourceUrl = getClass().getClassLoader().getResource("nextstep.txt");
        String file = resourceUrl.getFile();
        Path resourcePath = Paths.get(file);

        // 두 방법 모두로 파일 존재 여부와 권한을 확인해보자
        assertThat(Files.exists(relativePath)).isTrue();
        assertThat(Files.isReadable(relativePath)).isTrue();
        assertThat(Files.exists(resourcePath)).isTrue();
        assertThat(Files.isReadable(resourcePath)).isTrue();
    }

    /**
     * 7단계: 예외 처리하기
     * <p>
     * 파일 입출력 시 발생할 수 있는 예외를 처리해보자. - IOException 처리 - try-with-resources 사용 - 존재하지 않는 파일 처리
     */
    @Test
    void step7_예외_처리하기() {
        // try-catch를 사용해서 예외 처리를 해보자
        boolean exceptionHandled = false;
        try {
            Path path = Paths.get("존재하지_않는_파일.txt");
            List<String> lines = Files.readAllLines(path);
        } catch (IOException e) {
            exceptionHandled = true;
        }

        // 존재하지 않는 파일에 대한 안전한 접근을 해보자
        Path nonExistentPath = Paths.get("존재하지_않는_파일.txt");
        boolean safeExists = Files.exists(nonExistentPath);

        // 아래 주석을 해제하고 테스트를 통과시켜보자
        assertThat(exceptionHandled).isTrue();
        assertThat(safeExists).isFalse();
    }

    /**
     * 8단계: 디렉토리 내용 확인하기
     * <p>
     * 디렉토리 내의 파일들을 확인해보자. - Files.list() - 디렉토리 순회 - 현재 작업 디렉토리와 리소스 디렉토리 비교
     */
    @Test
    void step8_디렉토리_내용_확인하기() throws IOException {
        // 현재 작업 디렉토리의 Path를 가져와보자
        Path currentDir = Paths.get(".");

        // src/test/resources 디렉토리의 Path를 가져와보자
        Path resourcesDir = Paths.get("src/test/resources");

        // 두 디렉토리 내의 파일들을 확인해보자
        List<Path> currentFiles = Files.list(currentDir).toList();
        List<Path> resourceFiles = Files.list(resourcesDir).toList();

        // 아래 주석을 해제하고 테스트를 통과시켜보자
        assertThat(currentFiles).isNotEmpty();
        assertThat(resourceFiles).isNotEmpty();
        assertThat(resourceFiles).anyMatch(path -> path.getFileName().toString().equals("nextstep.txt"));
    }

    /**
     * 9단계: 파일 메타데이터 확인하기
     * <p>
     * 파일의 상세한 정보를 확인해보자. - 생성 시간 - 수정 시간 - 파일 속성 - 다양한 경로 방식으로 메타데이터 확인
     */
    @Test
    void step9_파일_메타데이터_확인하기() throws IOException {
        // 상대 경로로 Path 객체를 생성해보자
        Path path = Paths.get("src/test/resources/nextstep.txt");

        // 파일의 기본 속성들을 확인해보자
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        FileTime creationTime = attrs.creationTime();
        FileTime lastModifiedTime = attrs.lastModifiedTime();
        long size = attrs.size();

        // 아래 주석을 해제하고 테스트를 통과시켜보자
        assertThat(creationTime).isNotNull();
        assertThat(lastModifiedTime).isNotNull();
        assertThat(size).isGreaterThan(0);
    }
}
