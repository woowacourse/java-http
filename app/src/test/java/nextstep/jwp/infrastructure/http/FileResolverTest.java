package nextstep.jwp.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileResolverTest {

    private FileResolver fileResolver;

    @BeforeEach
    void setUp() {
        fileResolver = new FileResolver("static");
    }

    @DisplayName("입력한 경로의 파일 내용을 반환한다.")
    @Test
    void read() {
        assertThat(fileResolver.read("/hello.html")).isEqualTo("Hello world!");
    }

    @DisplayName("입력한 경로의 파일의 Content-Type을 반환")
    @Test
    void contentType() {
        assertThat(fileResolver.contentType("/hello.html")).isEqualTo("text/html");
        assertThat(fileResolver.contentType("/assets/img/error-404-monochrome.svg")).isEqualTo("image/svg+xml");
    }

    @DisplayName("존재하지 않는 파일의 경로면 예외처리")
    @Test
    void invalidPath() {
        assertThatIllegalArgumentException().isThrownBy(() -> fileResolver.read("nonononono.html"));
    }
}