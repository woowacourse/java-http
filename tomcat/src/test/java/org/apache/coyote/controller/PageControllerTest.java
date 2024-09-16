package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Method;
import org.apache.coyote.http11.RequestLine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

class PageControllerTest {

    @DisplayName("/ 경로이면 /home.html로 반환한다.")
    @Test
    void doGet_whenDefault() throws IOException {
        // given
        String defaultPath = "/";
        String expectedPath = "/home.html";

        RequestLine requestLine = new RequestLine(Method.GET, defaultPath, "HTTP/1.1");
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders);
        PageController pageController = new PageController();

        // when
        HttpResponse httpResponse = pageController.doGet(httpRequest);
        String resource = getResource(expectedPath);

        // then
        Assertions.assertThat(httpResponse.getContentLength()).isEqualTo(resource.length());
    }

    //TODO 리팩토링
    private String getResource(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + path);
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}