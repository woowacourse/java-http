package nextstep.jwp.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class ViewTest {

    @DisplayName("Javascript 경로를 이용하여 View를 생성한다 - 성공")
    @Test
    void createJavascriptView() throws IOException {
        // given
        final URL resource = getClass().getClassLoader().getResource("static/assets/chart-area.js");
        final Path path = Paths.get(resource.getPath());
        final byte[] bytes = Files.readAllBytes(path);

        // when
        final View view = new View("/assets/chart-area.js");
        final byte[] bytes1 = view.render().getBytes();

        // then
        assertThat(bytes1).isEqualTo(bytes);
    }

    @DisplayName("CSS 경로를 이용하여 View를 생성한다 - 성공")
    @Test
    void createCSSView() throws IOException {
        // given
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final Path path = Paths.get(resource.getPath());
        final byte[] bytes = Files.readAllBytes(path);

        // when
        final View view = new View("/css/styles.css");
        final byte[] bytes1 = view.render().getBytes();

        // then
        assertThat(bytes1).isEqualTo(bytes);
    }
}