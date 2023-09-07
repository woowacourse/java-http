package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.Charset.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpContentTypeTest {

    @Test
    void 파일_확장자로_Http_Content를_얻는다() {
        // given
        String fileExtension = "html";

        // when
        HttpContentType httpContentType = HttpContentType.getByFileExtension(fileExtension);

        // then
        assertThat(httpContentType).isEqualTo(HttpContentType.TEXT_HTML);
    }

    @Test
    void mimeType을_charset과_함께_반환한다() {
        // given
        HttpContentType httpContentType = HttpContentType.TEXT_HTML;

        // when
        String mimeTypeWithCharset = httpContentType.mimeTypeWithCharset(UTF_8);

        // then
        assertThat(mimeTypeWithCharset).isEqualTo("text/html;charset=utf-8");
    }
}
