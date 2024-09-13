package com.techcourse.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class HelloControllerTest {

    @Test
    @DisplayName("GET 요청을 처리할 수 있다.")
    void doGet() throws Exception {
        HelloController helloController = new HelloController();
        HttpResponse response = HttpResponse.createHttp11Response();

        helloController.doGet(Mockito.mock(HttpRequest.class), response);

        assertThat(response.getBody()).isEqualTo("Hello world!");
    }
}
