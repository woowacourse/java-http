package com.techcourse.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultControllerTest {

    private static final String DEFAULT_TEXT = "Hello world!";
    private static final String LOCATION_404_HEADER = "Location: 404.html";

    private final DefaultController defaultController;


    public DefaultControllerTest() {
        this.defaultController = new DefaultController();
    }

    @Test
    @DisplayName("/로 요청이 들어오는 경우 true를 반환한다.")
    void canHandle_WhenDefaultPath() {
        HttpRequest httpRequest = HttpRequestFixture.GET_DEFAULT_PATH_REQUEST;

        assertTrue(defaultController.canHandle(httpRequest));
    }

    @Test
    @DisplayName("/로 요청이 들어오지 않는 경우 false 반환한다.")
    void canNotHandle_WhenNotDefaultPath() {
        HttpRequest httpRequest = HttpRequestFixture.POST_REGISTER_PATH_REQUEST;

        assertFalse(defaultController.canHandle(httpRequest));
    }

    @Test
    @DisplayName("/로 Get 요청이 들어오는 경우 고정된 텍스트를 출력한다.")
    void responseResponseBody_WhenGetDefaultPath() {
        HttpRequest httpRequest = HttpRequestFixture.GET_DEFAULT_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        defaultController.doGet(httpRequest, httpResponse);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(DEFAULT_TEXT));
    }

    @Test
    @DisplayName("/로 Post 요청이 들어오는 경우 에러 페이지로 리다이렉트 한다.")
    void redirectErrorPage_WhenPostDefaultPath() {
        HttpRequest httpRequest = HttpRequestFixture.POST_DEFAULT_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        defaultController.doPost(httpRequest, httpResponse);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(LOCATION_404_HEADER));
    }
}
