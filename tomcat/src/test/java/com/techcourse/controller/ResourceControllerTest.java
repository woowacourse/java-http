package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.fixture.HttpRequestFixture;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceControllerTest {

    private ResourceController resourceController;

    @BeforeEach
    void setup() {
        resourceController = new ResourceController();
    }

    @DisplayName("파일 경로에 대해 존재하는 정적 파일을 응답 객체에 추가한다.")
    @Test
    void doGet() throws IOException {
        //given
        HttpRequest request = HttpRequestFixture.getGetRequest("/");
        HttpResponse response = new HttpResponse(request.getHttpVersion());

        //when
        resourceController.doGet(request, response);

        //then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        File file = new File(resource.getFile());
        String expected = new String(Files.readAllBytes(file.toPath()));

        assertThat(response.getBody()).isEqualTo(expected);
    }

    @DisplayName("파일 경로에 대해 존재하는 정적 파일이 존재하지 않으면 302 FOUND와 Location 헤더에 404 경로를 포함한다.")
    @Test
    void doGetThrowsException() {
        //given
        HttpRequest request = HttpRequestFixture.getGetRequest("/baeky");
        HttpResponse response = new HttpResponse(request.getHttpVersion());

        //when
        resourceController.doGet(request, response);

        //then
        assertAll(
                () -> assertThat(response.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getHeaders()).containsEntry(HttpHeaders.LOCATION, "/404")
        );
    }
}
