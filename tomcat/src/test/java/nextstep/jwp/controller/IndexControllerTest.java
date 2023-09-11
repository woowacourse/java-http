package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IndexControllerTest {

    private final Controller indexController = new IndexController();

    @Test
    @DisplayName("IndexController는 Post 요청을 지원하지 않는다.")
    void indexControllerNotSupportWhenPostMethod() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();
        String expected = String.join(System.lineSeparator(),
                "HTTP/1.1 302 FOUND ",
                "Location: /405.html ");

        //when
        indexController.service(RequestFixture.POST_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("IndexController는 GET 요청시, index.html을 반환한다.")
    void indexControllerSuccessServiceWhenGetMethod() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();
        URL url = getClass().getClassLoader().getResource("static/index.html");
        String expected = new String(Files.readAllBytes(Path.of(url.getPath())));

        //when
        indexController.service(RequestFixture.GET_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains("HTTP/1.1 200 OK")
                .contains(expected);
    }

}