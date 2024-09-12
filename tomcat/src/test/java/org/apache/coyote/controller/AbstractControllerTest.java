package org.apache.coyote.controller;

import com.techcourse.controller.TestController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @Test
    @DisplayName("")
    void test() throws Exception {
        TestController controller = new TestController();

        HttpRequest getRequest = HttpRequest.of("GET / HTTP/1.1");
        HttpRequest postRequest = HttpRequest.of("POST / HTTP/1.1");
        HttpResponse response = new HttpResponse();

        controller.service(getRequest, response);
        assertThat(response.toResponse()).contains("GET body");
        controller.service(postRequest, response);
        assertThat(response.toResponse()).contains("POST body");
    }

}
