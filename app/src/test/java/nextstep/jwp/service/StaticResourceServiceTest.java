package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.model.StaticResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StaticResourceServiceTest {

    private StaticResourceService staticResourceService = new StaticResourceService();

    @DisplayName("Uri 경로를 이용한 파일 탐색시")
    @Nested
    class findByPath {

        @DisplayName("성공하면 해당 파일을 반환한다.")
        @Test
        void ifSuccessReturnFile() throws IOException {
            // given
            String path = "/index.html";

            String expectContent = readFileContent(path);

            // when
            StaticResource resource = staticResourceService.findByPath(path);

            // then
            assertThat(resource.getContent()).isEqualTo(expectContent);
        }

        @DisplayName("실패하면 404.html 파일을 반환한다.")
        @Test
        void ifFailReturnNotFoundPage() throws IOException {
            // given
            String path = "/noneFilePath";

            String notFoundPageContent = readNotFoundPageContent();

            // when
            StaticResource resource = staticResourceService.findByPath(path);

            // then
            assertThat(resource.getContent()).isEqualTo(notFoundPageContent);
        }

        private String readFileContent(String path) throws IOException {
            URL url = ClassLoader.getSystemResource("static" + path);
            File file = new File(url.getFile());

            return new String(Files.readAllBytes(file.toPath()));
        }

        private String readNotFoundPageContent() throws IOException {
            return readFileContent("/404.html");
        }
    }
}