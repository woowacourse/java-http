package org.apache.coyote.http11.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.techcourse.controller.WelcomePageController;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpUrl;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WelcomePageControllerTest {

    @Test
    @DisplayName("StringHttpHandler는 요청에 대한 응답으로 정적 문자열을 반환한다.")
    void handleTest() throws Exception {
        // given
        WelcomePageController stringHttpHandler = new WelcomePageController();
        HttpRequest request = new HttpRequest(HttpMethod.GET, new HttpUrl("/"));

        // when
        HttpResponse response = new HttpResponse();
        stringHttpHandler.service(request, response);

        // then
        assertEquals("Hello world!", new String(response.getBody()));
    }
}
