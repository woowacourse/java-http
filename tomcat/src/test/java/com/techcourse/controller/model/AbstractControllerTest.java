package com.techcourse.controller.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.techcourse.controller.DefaultController;
import java.io.OutputStream;
import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    private static final String LOCATION_404_HEADER = "Location: 404.html";

    @Test
    @DisplayName("처리할 수 없는 메서드의 요청이 들어오는 경우 404 를 호출한다.")
    void service() {
        HttpRequest httpRequest = HttpRequestFixture.NO_EXIST_METHOD_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        Controller controller = new DefaultController();
        controller.service(httpRequest, httpResponse);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(LOCATION_404_HEADER));
    }
}
