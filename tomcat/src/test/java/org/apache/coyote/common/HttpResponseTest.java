package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpResponseTest {

    @Test
    void setContentBody_호출시_header의_content_length가_설정된다() {
        // given
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.OK);

        // when
        response.setContentBody("Hello world!");

        // then
        String actual = response.getHttpHeaders().getHeader("Content-Length").get(0);
        assertThat(actual).isEqualTo("12");
    }

    @Test
    void toBytes_성공() {
        // given
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.OK);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "max-age=0");
        response.addHeader("Cache-Control", "must-revalidate");
        response.setContentBody("Hello world!");

        // when
        byte[] expect = response.toBytes();

        // then
        byte[] actual = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html ",
            "Cache-Control: no-cache, max-age=0, must-revalidate ",
            "Content-Length: 12 ",
            "",
            "Hello world!"
        ).getBytes(StandardCharsets.UTF_8);

        // then
        assertThat(expect).isEqualTo(actual);
    }
}
