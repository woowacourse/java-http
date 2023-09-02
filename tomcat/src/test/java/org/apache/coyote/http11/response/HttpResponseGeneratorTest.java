package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.common.HttpStatus;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseGeneratorTest {

    private final HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator();

    @Test
    void 입력받은_ResponseEntity의_uri가_루트인_경우_Hello_world가_담긴_HttpResponse를_반환한다() throws IOException {
        // given
        final ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK, "/");

        // when
        final String actual = httpResponseGenerator.generate(responseEntity);

        // then
        final String expected = String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 입력받은_HttpStatus에_따라_HttpResponse의_상태가_달라진다() throws IOException {
        // given
        final ResponseEntity responseEntity = new ResponseEntity(HttpStatus.UNAUTHORIZED, "/");

        // when
        final String actual = httpResponseGenerator.generate(responseEntity);

        // then
        final String expected = String.join(
                "\r\n",
                "HTTP/1.1 401 UNAUTHORIZED ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 입력받은_uri에_해당하는_파일을_읽어와_HttpResponse를_반환한다() throws IOException {
        // given
        final ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK, "/index.html");

        // when
        final String actual = httpResponseGenerator.generate(responseEntity);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expected);
    }
}
