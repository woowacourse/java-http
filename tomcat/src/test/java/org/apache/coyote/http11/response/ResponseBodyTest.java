package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ResponseBodyTest {

    @Test
    void 확장자와_내용으로_응답을_생성한다() {
        // given
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>안녕하세요</h1>\n" +
                "</body>\n" +
                "</html>";
        ResponseBody responseBody = ResponseBody.of(html, "index.html");

        // when & then
        assertThat(responseBody.getContent()).isEqualTo(html);
        assertThat(responseBody.getLength()).isEqualTo(html.getBytes().length);
        assertThat(responseBody.getContentType()).isEqualTo(ContentType.HTML);
    }
}
