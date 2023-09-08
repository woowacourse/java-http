package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.common.PathUrl;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class FileUtilTest {

    @Test
    void 정적_요청일때_파일을_제대로_찾아오는지_확인한다() {
        final PathUrl pathUrl = PathUrl.from("/hi.html");
        final String resource = FileUtil.getResource(pathUrl);

        final String expect = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "    </body>\n" +
                "</html>\n" +
                "";

        assertThat(resource).isEqualTo(expect);
    }

    @Test
    void 정적_요청일때_파일이_없다면_404_html_을_띄운다() throws IOException {
        // given
        final PathUrl badPathUrl = PathUrl.from("/bad.html");
        final String actualResource = FileUtil.getResource(badPathUrl);

        // then
        final StringBuilder expected_404 = new StringBuilder();
        final URL resource = FileUtil.class.getClassLoader().getResource("static" + "/404.html");
        final Path path = Paths.get(resource.getPath());
        try (final BufferedReader fileReader = new BufferedReader(new FileReader(path.toFile()))) {
            fileReader.lines()
                    .forEach(br -> expected_404.append(br)
                            .append(System.lineSeparator()));
        }
        assertThat(actualResource).isEqualTo(expected_404.toString());
    }
}
