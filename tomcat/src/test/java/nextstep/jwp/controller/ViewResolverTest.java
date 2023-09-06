package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @Test
    void html_파일_내용을_읽어서_반환한다() throws IOException {
        final String readFile = ViewResolver.findView("index");

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String actualFile = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(readFile).isEqualTo(actualFile);
    }
}
