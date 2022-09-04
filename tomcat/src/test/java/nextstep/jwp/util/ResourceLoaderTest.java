package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceLoaderTest {

    @DisplayName("파일 명으로 정적 자원을 불러온다.")
    @Test
    void getStaticResource() throws IOException, URISyntaxException {
        final String resource = ResourceLoader.getStaticResource("/index.html");

        assertThat(resource).contains("대시보드");
    }

    @DisplayName("존재하지 않는 파일명을 입력하면 예외가 발생한다.")
    @Test
    void getStaticResource_ifFileNotFound() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> ResourceLoader.getStaticResource("/foo.txt"));
    }
}
