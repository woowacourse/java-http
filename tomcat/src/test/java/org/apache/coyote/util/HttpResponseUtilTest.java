package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseUtilTest {

    @Test
    void path와_responseBody를_받아_HttpResponse를_반환한다() {
        // given
        final String path = "/index.html";
        final String responseBody = "hello world";

        // when
        final String actual = HttpResponseUtil.generate(path, responseBody);

        // then
        final String expected = String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void path의_확장자가_css_타입인_경우_Content_Type이_text_css인_HttpResponse를_반환한다() {
        // given
        final String path = "/styles.css";
        final String responseBody = "hello world";

        // when
        final String actual = HttpResponseUtil.generate(path, responseBody);

        // then
        final String expected = String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
        assertThat(actual).isEqualTo(expected);
    }
}
