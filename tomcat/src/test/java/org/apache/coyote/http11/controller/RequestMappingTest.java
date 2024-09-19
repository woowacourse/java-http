package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.controller.NotFoundPageController;
import com.techcourse.controller.StaticResourceController;

class RequestMappingTest {

    @DisplayName("요청 uri의 파일 확장자가 유효한 타입이면 StaticResourceController 를 반환한다.")
    @Test
    void returnStaticResourceControllerWhenValidContentTypeUri() throws IOException {
        String httpRequest = "GET /test.html HTTP/1.1\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequest));
        HttpRequest request = HttpRequest.from(bufferedReader);
        assertThat(RequestMapping.from(request).getClass()).isEqualTo(StaticResourceController.class);
    }

    @DisplayName("요청 uri가 모든 조건에 맞지 않으면 NotFoundPageController 를 반환한다.")
    @Test
    void returnNotFoundPageControllerWhenInValidUri() throws IOException {
        String httpRequest = "GET /test HTTP/1.1\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequest));
        HttpRequest request = HttpRequest.from(bufferedReader);
        assertThat(RequestMapping.from(request).getClass()).isEqualTo(NotFoundPageController.class);
    }
}
