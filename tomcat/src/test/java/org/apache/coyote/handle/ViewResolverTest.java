package org.apache.coyote.handle;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ViewResolverTest {

    @Nested
    class 페이지_렌더링 {

        @Test
        void 존재하는_페이지라면_해당_페이지를_렌더링한다() throws Exception {
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);
            final HttpStatus httpStatus = HttpStatus.OK;
            final String page = "index.html";

            final ViewResolver viewResolver = ViewResolver.getInstance();
            viewResolver.renderPage(httpResponse, httpStatus, page);

            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 5564 ",
                    "",
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
            );
            assertThat(httpResponse).hasToString(expected);
        }

        @Test
        void 존재하지_않는_페이지라면_404_페이지를_렌더링한다() throws Exception {
            final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);
            final HttpStatus httpStatus = HttpStatus.OK;
            final String page = "hello.html";

            final ViewResolver viewResolver = ViewResolver.getInstance();
            viewResolver.renderPage(httpResponse, httpStatus, page);

            final URL resource = getClass().getClassLoader().getResource("static/404.html");
            final String expected = String.join("\r\n",
                    "HTTP/1.1 404 Not Found ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 2426 ",
                    "",
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
            );
            assertThat(httpResponse).hasToString(expected);
        }
    }
}
