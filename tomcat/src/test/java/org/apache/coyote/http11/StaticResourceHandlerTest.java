package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StaticResourceHandlerTest {

    @Test
    void JavaScript_파일을_요청하면_파일과_JavaScript_Content_Type을_응답한다() throws IOException {
        // given
        StaticResourceHandler handler = new StaticResourceHandler(getClass().getClassLoader());
        byte[] expected = getClass().getResourceAsStream("/static/assets/chart-area.js").readAllBytes();

        // when
        ByteArrayOutputStream output = write(handler.respond("/assets/chart-area.js"));

        // then
        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK")
                .containsPattern("Content-Type: (text|application)/javascript");
        assertThat(output.toByteArray()).endsWith(expected);
    }

    @Test
    void 이미지_파일을_요청하면_원본_바이트를_그대로_응답한다() throws IOException {
        // given
        byte[] bytes = {(byte) 0x89, 0x50, 0x4e, 0x47, (byte) 0xff, 0};
        ClassLoader loader = mock(ClassLoader.class);
        when(loader.getResourceAsStream("static/image.png")).thenReturn(new ByteArrayInputStream(bytes));
        StaticResourceHandler handler = new StaticResourceHandler(loader);

        // when
        ByteArrayOutputStream output = write(handler.respond("/image.png"));

        // then
        assertThat(output.toByteArray()).endsWith(bytes);
        assertThat(output.toString(StandardCharsets.ISO_8859_1)).contains("Content-Type: image/png", "Content-Length: 6");
    }

    @Test
    void 알_수_없는_확장자의_파일도_바이너리_Content_Type으로_응답한다() throws IOException {
        // given
        ClassLoader loader = mock(ClassLoader.class);
        when(loader.getResourceAsStream("static/file.unmappedextension"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));
        StaticResourceHandler handler = new StaticResourceHandler(loader);

        // when
        ByteArrayOutputStream output = write(handler.respond("/file.unmappedextension"));

        // then
        assertThat(output.toString()).contains("Content-Type: application/octet-stream");
        assertThat(output.toByteArray()).endsWith(new byte[]{1, 2, 3});
    }

    @Test
    void 정적_디렉터리_밖의_파일을_요청하면_404를_응답한다() throws IOException {
        // given
        StaticResourceHandler handler = new StaticResourceHandler(getClass().getClassLoader());

        // when
        ByteArrayOutputStream output = write(handler.respond("/../outside.html"));

        // then
        assertThat(output.toString()).startsWith("HTTP/1.1 404 Not Found");
    }

    @Test
    void 오류_페이지를_읽지_못해도_원래_오류_상태와_기본_본문을_응답한다() throws IOException {
        // given
        ClassLoader loader = mock(ClassLoader.class);
        InputStream broken = mock(InputStream.class);
        when(broken.readAllBytes()).thenThrow(new IOException("read failed"));
        when(loader.getResourceAsStream("static/500.html")).thenReturn(broken);
        StaticResourceHandler handler = new StaticResourceHandler(loader);

        // when
        ByteArrayOutputStream output = write(handler.error(HttpStatus.INTERNAL_SERVER_ERROR));

        // then
        assertThat(output.toString()).startsWith("HTTP/1.1 500 Internal Server Error")
                .endsWith("\r\n\r\nInternal Server Error");
    }

    private ByteArrayOutputStream write(HttpResponse response) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        response.writeTo(output);
        return output;
    }
}
