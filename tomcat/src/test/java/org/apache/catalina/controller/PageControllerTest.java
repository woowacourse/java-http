package org.apache.catalina.controller;

import org.apache.catalina.ResourceManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestHeaders;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Method;
import org.apache.coyote.http11.RequestLine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PageControllerTest {

    @DisplayName("/ 경로이면 /home.html로 반환한다.")
    @Test
    void doGet_whenDefault() throws Exception {
        // given
        String defaultPath = "/";
        String expectedPath = "/home.html";

        RequestLine requestLine = new RequestLine(Method.GET, defaultPath, "HTTP/1.1");
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders();
        HttpRequest httpRequest = new HttpRequest(requestLine, httpRequestHeaders);
        AbstractController pageController = new PageController();

        HttpResponse httpResponse = new HttpResponse();

        // when
        pageController.service(httpRequest, httpResponse);
        String resource = ResourceManager.getFileResource(expectedPath);

        // then
        Assertions.assertThat(httpResponse.getContentLength()).isEqualTo(resource.length());
    }
}
