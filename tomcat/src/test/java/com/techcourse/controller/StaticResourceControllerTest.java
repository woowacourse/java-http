package com.techcourse.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.ResourceFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceControllerTest {

    private static final String LOCATION_404_HEADER = "Location: 404.html";
    private static final String INDEX_HTML_PATH = "/index.html";

    private final StaticResourceController staticResourceController;

    public StaticResourceControllerTest() {
        this.staticResourceController = new StaticResourceController();
    }

    @Test
    @DisplayName("파일이 존재하는 요청일 경우 true를 반환한다.")
    void canHandle_WhenFileExistPath() {
        HttpRequest httpRequest = HttpRequestFixture.GET_STATIC_MAIN_PATH_REQUEST;

        assertTrue(staticResourceController.canHandle(httpRequest));
    }

    @Test
    @DisplayName("파일이 존재하지 않는 요청일 경우 false를 반환한다.")
    void canHandle_WhenFileNotExistPath() {
        HttpRequest httpRequest = HttpRequestFixture.GET_DEFAULT_PATH_REQUEST;

        assertFalse(staticResourceController.canHandle(httpRequest));
    }

    @Test
    @DisplayName("파일이 존재하나 Post로 요청을 보낸 경우 에러 페이지로 리다이렉트 한다.")
    void redirectErrorPage_WhenPostResource() {
        HttpRequest httpRequest = HttpRequestFixture.POST_STATIC_MAIN_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        staticResourceController.doPost(httpRequest, httpResponse);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(LOCATION_404_HEADER));
    }

    @Test
    @DisplayName("존재하는 파일로 Get 요청을 보내는 경우 해당 파일을 반환한다.")
    void responseResource_WhenGetExistResource() {
        HttpRequest httpRequest = HttpRequestFixture.GET_STATIC_MAIN_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        staticResourceController.doGet(httpRequest, httpResponse);

        String resource = ResourceFinder.findBy(INDEX_HTML_PATH);
        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(resource));
    }
}
