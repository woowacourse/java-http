package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseGeneratorTest {

    private final HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator();

    @Test
    void 입력받은_uri에_해당하는_파일을_읽어와_HttpResponse를_반환한다() throws IOException {
        // given
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1)
                .setHttpStatus(HttpStatus.OK)
                .sendRedirect("/index.html");

        // when
        final String actual = httpResponseGenerator.generate(httpResponse);

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
