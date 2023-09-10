package org.apache.coyote.http11;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.PathUrl;
import org.apache.coyote.response.HttpResponse;
import org.apache.exception.PageRedirectException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
class FileUtilTest {

    @Test
    void 정적_요청일때_파일을_제대로_찾아오는지_확인한다() {
        final PathUrl pathUrl = PathUrl.from("/hi.html");
        final String resource = FileUtil.getResource(HttpResponse.create(HttpVersion.HTTP11), pathUrl);

        final String expect = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "    </body>\n" +
                "</html>\n";

        assertThat(resource).isEqualTo(expect);
    }

    @Test
    void 정적_요청일때_파일이_없다면_404페이지로_리다이렉트_하기_위한_예외를_던진다() {
        // given
        final PathUrl badPathUrl = PathUrl.from("/bad.html");

        assertThatThrownBy(() -> FileUtil.getResource(HttpResponse.create(HttpVersion.HTTP11), badPathUrl))
                .isExactlyInstanceOf(PageRedirectException.PageNotFound.class);
    }
}
