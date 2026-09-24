package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.StaticResourceLoader;
import org.apache.coyote.http11.request.HttpCookie;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InternalServerErrorHandlerTest {
    @Test
    void fallsBackToPlainTextWhenErrorPageCannotBeLoaded() {
        StaticResourceLoader failingLoader = new StaticResourceLoader() {
            @Override
            public StaticResource load(String path) throws IOException {
                throw new IOException("500 페이지를 읽을 수 없습니다.");
            }
        };
        HttpResponse response = new HttpResponse();
        response.sendRedirect("/login");
        response.addCookie(HttpCookie.JSESSIONID, "old-session");

        new InternalServerErrorHandler(failingLoader).handle(response);

        String actual = new String(response.toBytes(), StandardCharsets.UTF_8);
        assertThat(actual)
                .startsWith("HTTP/1.1 500 Internal Server Error \r\nContent-Type: text/plain;charset=utf-8 ")
                .endsWith("Internal Server Error")
                .doesNotContain("Location:", "Set-Cookie:");
    }
}
