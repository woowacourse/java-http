package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.HtmlLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlLoaderTest {

    @Test
    void generateFile() throws IOException {
        // when
        String file = HtmlLoader.generateFile("static/index.html");

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(file).isEqualTo(body);
    }

    @Test
    void generateFile_noExtension() throws IOException {
        // when
        String file = HtmlLoader.generateFile("static/login");

        // then
        assertThat(file).isEqualTo("Hello world!");
    }

    @Test
    void generateFile_notExist() throws IOException {
        // when
        String file = HtmlLoader.generateFile("static/alpha.js");

        // then
        assertThat(file).isEqualTo("Hello world!");
    }
}
