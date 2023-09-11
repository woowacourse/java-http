package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    private final Controller homeController = new HomeController();

    @Test
    @DisplayName("HomeController는 POST 요청을 지원하지 않는다.")
    void homeControllerNotSupportWhenPostMethod() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();
        String expected = String.join(System.lineSeparator(),
                "HTTP/1.1 302 FOUND ",
                "Location: /405.html ");

        //when
        homeController.service(RequestFixture.POST_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("HomeController는 GET 요청시, 'Hello world!'를 반환한다.")
    void homeControllerSuccessServiceWhenGetMethod() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();

        //when
        homeController.service(RequestFixture.GET_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains("HTTP/1.1 200 OK")
                .contains("Hello world!");

    }
}