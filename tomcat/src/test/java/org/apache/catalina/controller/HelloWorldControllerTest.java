package org.apache.catalina.controller;

import org.apache.catalina.HttpRequestFixture;
import org.apache.catalina.response.ContentType;
import org.apache.catalina.response.Header;
import org.apache.catalina.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HelloWorldControllerTest {

    @DisplayName("GET 요청에 Hello world! 를 바디에 담아 반환한다.")
    @Test
    void doGet() {
        HelloWorldController controller = new HelloWorldController();
        HttpResponse response = new HttpResponse();

        controller.doGet(HttpRequestFixture.getHttpGetRequest("/"), response);

        assertAll(
                () -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200),
                () -> assertThat(response.getStatusLine().getStatusMessage()).isEqualTo("OK"),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(12),
                () -> assertThat(response.getBody()).isEqualTo("Hello world!")
        );
    }
}
