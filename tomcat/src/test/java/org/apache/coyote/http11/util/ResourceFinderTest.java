package org.apache.coyote.http11.util;

import javassist.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceFinderTest {

    @DisplayName("리소스를 파일 명으로 가져온다")
    @Test
    void returns_resource_by_file_name() throws IOException, URISyntaxException, NotFoundException {
        String resource = ResourceFinder.getStaticResource("/index.html");

        assertThat(resource).contains("대시보드");
    }

    @DisplayName("파일이 없다면 예외를 발생한다")
    @Test
    void throws_exception_when_file_empty() {
        assertThatThrownBy(() -> ResourceFinder.getStaticResource("/empty.file"))
                .isInstanceOf(NotFoundException.class);
    }
}
