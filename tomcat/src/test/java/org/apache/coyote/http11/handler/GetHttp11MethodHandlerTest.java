package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class GetHttp11MethodHandlerTest {

    private final GetHttp11MethodHandler handler = new GetHttp11MethodHandler();

    @Test
    void 요청_경로가_비어있으면_기본_처리를_한다() {
        // given
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Content-Type: text/html;charset=utf-8",
                "",
                "");

        // when
        String result = handler.handle(request);

        // then
        assertThat(result).contains("Hello world!");
    }

    @Test
    void 요청_경로가_존재하면_해당_경로의_정적_리소스를_문자열로_반환한다() {
        // given
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Content-Type: text/html;charset=utf-8",
                "",
                "");

        // when
        String result = handler.handle(request);

        // then
        assertThat(result).contains("<title>대시보드</title>");
    }

    @Test
    void 요청_경로에_html이_포함되면_Content_Type은_text_html_이다() {
        // given
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Content-Type: text/html;charset=utf-8",
                "",
                "");

        // when
        String result = handler.handle(request);

        // then
        assertThat(result).contains("Content-Type: text/html");
    }

    @Test
    void 요청_경로가_css이면_Content_Type은_text_css이다() {
        // given
        String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        // when
        String result = handler.handle(request);

        // then
        assertThat(result).contains("Content-Type: text/css");
    }
}
