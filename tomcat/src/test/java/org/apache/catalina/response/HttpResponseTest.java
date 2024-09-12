package org.apache.catalina.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpResponseTest {

    @DisplayName("응답을 forward하면 응답코드는 200이며 path 경로의 resource를 body에 담는다.")
    @Test
    void forward() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.forward("/index.html");

        File file = new File(getClass().getClassLoader().getResource("static/index.html").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(httpResponse.getBody()).isEqualTo(body),
                () -> assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200),
                () -> assertThat(httpResponse.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML),
                () -> assertThat(httpResponse.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(body.getBytes().length)
        );
    }

    @DisplayName("응답을 redirect하면 응답코드는 304이며 path 경로를 location 헤더에 저장한다.")
    @Test
    void sendRedirect() throws IOException {

        HttpResponse httpResponse = new HttpResponse();
        httpResponse.sendRedirect("/index.html");

        File file = new File(getClass().getClassLoader().getResource("static/index.html").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(httpResponse.getBody()).isEqualTo(body),
                () -> assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(302),
                () -> assertThat(httpResponse.getHeaders().get(Header.LOCATION)).isEqualTo("/index.html"),
                () -> assertThat(httpResponse.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML),
                () -> assertThat(httpResponse.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(body.getBytes().length)
        );
    }
}
