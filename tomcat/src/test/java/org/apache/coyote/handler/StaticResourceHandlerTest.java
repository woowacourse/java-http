package org.apache.coyote.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponseGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceHandlerTest {

    @Test
    @DisplayName("정적 리소스 처리: 기본적으로 /static 경로에 있는 리소스를 반환")
    void handle() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/404.html");
        final String responseBody = Files.readString(Path.of(resourceURL.getPath()));

        final StaticResourceHandler handler = StaticResourceHandler.getInstance();
        final String response = handler.handle(new HttpRequest("GET", "404.html", "HTTP/1.1", null, null));

        assertThat(response).contains(responseBody);
    }

    @Test
    @DisplayName("정적 리소스 처리: 확장자에 따라 다른 경로에 있는 리소스를 반환")
    void handle_When_Different_Extension_Resource() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/css/styles.css");
        final String responseBody = Files.readString(Path.of(resourceURL.getPath()));

        final StaticResourceHandler handler = StaticResourceHandler.getInstance();
        final String response = handler.handle(new HttpRequest("GET", "styles.css", "HTTP/1.1", null, null));

        assertThat(response).contains(responseBody);
    }
}
