package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class StaticResourceRendererTest {

    @Test
    void 명시된_로그인_페이지의_내용을_응답한다() throws Exception {
        StaticResourceRenderer renderer = new StaticResourceRenderer();
        HttpResponse response = new HttpResponse();

        renderer.writeResource("/login.html", response);

        String actual = new String(response.toByteArray(), StandardCharsets.UTF_8);

        assertThat(actual).startsWith("HTTP/1.1 200 OK");
        assertThat(actual).contains("<form method=\"post\" action=\"login\">");
        assertThat(actual).contains("Content-Type: text/html;charset=utf-8");
    }

    @Test
    void 실제_리소스_경로가_CSS이면_CSS_Content_Type을_응답한다() throws Exception {
        StaticResourceRenderer renderer = new StaticResourceRenderer();
        HttpResponse response = new HttpResponse();

        renderer.writeResource("/css/styles.css", response);

        String actual = new String(response.toByteArray(), StandardCharsets.UTF_8);

        assertThat(actual).startsWith("HTTP/1.1 200 OK");
        assertThat(actual).contains("Content-Type: text/css;charset=utf-8");
        assertThat(actual).doesNotEndWith("Not Found");
    }

    @Test
    void 없는_리소스는_404로_응답한다() throws Exception {
        StaticResourceRenderer renderer = new StaticResourceRenderer();
        HttpResponse response = new HttpResponse();

        renderer.writeResource("/missing.html", response);

        String actual = new String(response.toByteArray(), StandardCharsets.UTF_8);

        assertThat(actual).startsWith("HTTP/1.1 404 Not Found");
        assertThat(actual).contains("Content-Type: text/plain;charset=utf-8");
        assertThat(actual).endsWith("Not Found");
    }
}
