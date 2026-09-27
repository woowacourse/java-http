package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class StaticResourceTest {

    @Test
    void 확장자가_없는_경로는_html_파일을_응답한다() throws IOException {
        assertThat(page("/login"))
                .contains("Content-Type: text/html;charset=utf-8 ")
                .contains("<title>로그인</title>");
    }

    @Test
    void 확장자에_맞는_컨텐츠_타입으로_응답한다() throws IOException {
        assertThat(page("/css/styles.css"))
                .contains("Content-Type: text/css;charset=utf-8 ");
    }

    private String page(String path) throws IOException {
        HttpResponse response = HttpResponse.empty();
        response.page(path);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.writeTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
