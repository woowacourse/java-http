package com.techcourse.controller.model;

import com.techcourse.controller.DefaultController;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    private static final String LOCATION_404_HEADER = "Location: 404.html";

    @Test
    @DisplayName("처리할 수 없는 메서드의 요청이 들어오는 경우 404 를 호출한다.")
    void service() {
        HttpRequest httpRequest = HttpRequestFixture.NO_EXIST_METHOD_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        Controller controller = new DefaultController();
        controller.service(httpRequest, httpResponse);

        assertThat(new String(httpResponse.combineResponseToBytes())).contains(LOCATION_404_HEADER);
    }
}
