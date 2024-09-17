package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    void doGet() {
        // given
        HomeController homeController = new HomeController();
        HttpRequest request = new HttpRequest(List.of("GET / HTTP/1.1"), "");
        HttpResponse response = new HttpResponse();

        // when
        homeController.doGet(request, response);

        // then
        assertAll(
                () -> assertThat(response.getReponse()).contains("200 OK"),
                () -> assertThat(response.getReponse()).contains("<title>대시보드</title>")
        );
    }
}
