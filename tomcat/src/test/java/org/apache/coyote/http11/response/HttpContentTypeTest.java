package org.apache.coyote.http11.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.header.HttpContentType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpContentTypeTest {

    @Test
    void 파일_확장자와_charset으로_mimeType_메시지를_얻는다() {
        // given
        String fileExtension = "html";

        // when
        String mimeTypeWithCharset = HttpContentType.mimeTypeWithCharset(fileExtension, UTF_8);

        // then
        assertThat(mimeTypeWithCharset).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void content_Type과_charset으로_mimeType_메시지를_얻는다() {
        // given
        HttpContentType httpContentType = HttpContentType.TEXT_HTML;

        // when
        String mimeTypeWithCharset = httpContentType.mimeTypeWithCharset(UTF_8);

        // then
        assertThat(mimeTypeWithCharset).isEqualTo("text/html;charset=utf-8");
    }
}
