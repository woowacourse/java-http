package com.techcourse.controller;

import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpStatus;
import jakarta.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class NotFoundControllerTest {

    @Test
    @DisplayName("404 응답을 처리할 수 있다.")
    void service() throws Exception {
        NotFoundController notFoundController = new NotFoundController();
        HttpResponse response = HttpResponse.createHttpResponse(HttpVersion.HTTP_1_1);

        notFoundController.service(Mockito.mock(HttpRequest.class), response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
