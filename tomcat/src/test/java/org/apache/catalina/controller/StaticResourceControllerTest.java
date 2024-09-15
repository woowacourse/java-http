package org.apache.catalina.controller;

import org.apache.catalina.HttpRequestFixture;
import org.apache.catalina.response.ContentType;
import org.apache.catalina.response.Header;
import org.apache.catalina.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaticResourceControllerTest {

    private final StaticResourceController staticResourceController = new StaticResourceController();

    @DisplayName("요청한 정적 파일을 응답한다.")
    @Test
    void doGet() throws IOException {
        HttpResponse response = new HttpResponse();
        staticResourceController.doGet(HttpRequestFixture.getHttpGetRequest("/css/styles.css"), response);

        File file = new File(getClass().getClassLoader().getResource("static/css/styles.css").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200),
                () -> assertThat(response.getStatusLine().getStatusMessage()).isEqualTo("OK"),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.CSS.value()),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }
}
