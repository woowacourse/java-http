package com.techcourse.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class NotFoundControllerTest {

    @Test
    @DisplayName("404 응답을 처리할 수 있다.")
    void service() throws Exception {
        NotFoundController notFoundController = new NotFoundController();
        HttpResponse response = HttpResponse.createHttp11Response();

        notFoundController.service(Mockito.mock(HttpRequest.class), response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
